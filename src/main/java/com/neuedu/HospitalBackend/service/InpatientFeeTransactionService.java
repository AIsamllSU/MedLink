package com.neuedu.HospitalBackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.InpatientFeeLedgerDTO;
import com.neuedu.HospitalBackend.dto.InpatientFeeRequest;
import com.neuedu.HospitalBackend.dto.InpatientFeeTransactionDTO;
import com.neuedu.HospitalBackend.entity.ChargeCatalogItem;
import com.neuedu.HospitalBackend.entity.InpatientAdmission;
import com.neuedu.HospitalBackend.entity.InpatientFeeTransaction;
import com.neuedu.HospitalBackend.mapper.ChargeCatalogItemMapper;
import com.neuedu.HospitalBackend.mapper.InpatientAdmissionMapper;
import com.neuedu.HospitalBackend.mapper.InpatientFeeTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * 住院查询、患者查询、费用汇总、流水登记
 */
@Service
@RequiredArgsConstructor
public class InpatientFeeTransactionService {
    //住院登记Mapper依赖
    private final InpatientAdmissionMapper admissionMapper;
    //住院流水Mapper依赖
    private final InpatientFeeTransactionMapper transactionMapper;
    //医院字典依赖
    private final ChargeCatalogItemMapper catalogItemMapper;

    //1、查询住院登记记录并转换为页面响应的DTO
    public List<InpatientAdmission> listAdmissions(){
      return  admissionMapper.selectList(new QueryWrapper<InpatientAdmission>().orderByDesc("created_at"));
    }

    //2、根据住院登记ID查询账户台账
    //校验：根据登记id查询住院登记记录，多个方法同时用，单独定义方法
    public InpatientFeeLedgerDTO ledger(Long admissionId){
        //1、根据id查询当前所有的流水信息按照创建时间降序排序
        InpatientAdmission admission = requireAdmission(admissionId);
        List<InpatientFeeTransaction> transactions = transactionMapper.selectList(new QueryWrapper<InpatientFeeTransaction>
                ().eq("admission_id", admissionId).orderByDesc("created_at"));
        //2、拿到数据后
        // BigDecimal totalCharges,
        // BigDecimal totalPayments,
        // BigDecimal totalRefunds 这三个数据给一个默认值，防止为空
        BigDecimal charges = BigDecimal.ZERO;
        BigDecimal payments = BigDecimal.ZERO;
        BigDecimal refunds = BigDecimal.ZERO;
        //3、遍历集合，判断业务类型、计算金额（总费用、总支付费用、退费总费用）
        for(InpatientFeeTransaction transaction:transactions){
            if("DEPOSIT".equals(transaction.getTransactionType())){
                //交押金  （预交押金/补缴押金-->累加到payments(患者交给医院的钱)）
                payments = payments.add(transaction.getAmount());
            }else if("CHARGE".equals(transaction.getTransactionType())){
                //计算扣费 （医疗计费（床位、药品、检查）-->累计到charge(患者消费)）
                charges = charges.add(transaction.getAmount());
            }else if("REFUND".equals(transaction.getTransactionType())){
                //退费（退押金、退药-->累加到refund中（医院退给患者的钱））
                refunds = refunds.add(transaction.getAmount());
            }
        }
        //4、计算余额（注意，BigDecimal不能直接使用+ - 要用add/subtract）
        BigDecimal initialDeposit = zero(admission.getDeposit());
        BigDecimal balance = initialDeposit.add(payments).subtract(charges).subtract(refunds);
        //5、向前端返回台账信息
        return new InpatientFeeLedgerDTO(
                admission.getId(),admission.getInpatientNo(),admission.getCaseNo(),
                admission.getName(),initialDeposit,charges,payments,refunds,balance,
                transactions.stream().map(this::toDto).toList()
        );
    }


    //3、校验住院账户余额限制、保存流水并返回最新的台账
    @Transactional //开启事务
    public InpatientFeeLedgerDTO createTransaction(Long admissionId, InpatientFeeRequest request){
        //校验id是否有流水记录
        InpatientAdmission admission = requireAdmission(admissionId);
        //费用记录不能为空
        if(request == null){
            throw new IllegalArgumentException("费用记录不能为空");
        }
        //获取请求类型
        String type = request.transactionType() == null?"":request.transactionType().toUpperCase(Locale.ROOT);
        System.out.println("==============="+type);
        //住院费用类型必须匹配 "DEPOSIT" "CHARGE" "REFUND"
        if(!List.of("DEPOSIT","CHARGE","REFUND").contains(type)){
            throw new IllegalArgumentException("不支持的住院费用类型");
        }
        /**
         * 1、判断项目是否有校并且数量大于0
         * 2、判断收费项目是否存在或已停用
         * 3、缴费或退款金额必须大于0
         */
        BigDecimal amount;//初始化金额
        Integer quantity = request.quantity();//数量
        BigDecimal unitPrice = BigDecimal.ZERO;//单价
        ChargeCatalogItem catalogItem = null;//收费项目
        //如果是扣费
        if("CHARGE".equals(type)){
            if(quantity == null || quantity<=0 ||request.catalogItemId() == null){
                throw new IllegalArgumentException("请选择有效项目并填写大于0的数量");
            }
            //在上面把字典依赖引入 catalogItemMapper在最上面去定义！！！  根据id查询收费项目
            catalogItem = catalogItemMapper.selectById(request.catalogItemId());
            if(catalogItem == null || !"ACTIVE".equals(catalogItem.getStatus())){
                throw new IllegalArgumentException("收费项目不存在或已停用");
            }
            //计算总金额
            unitPrice = catalogItem.getPrice();//获取字典中的单价
            //总金额 = 单价 * 数量
            amount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }else{
            amount = request.amount();//获取前端传递的值
            if(amount==null||amount.signum()<=0){
                throw new IllegalArgumentException("缴费或退款金额必须大于0");
            }
            //数量默认1
            quantity = 1;
            unitPrice = amount;
        }
        //验证余额是否不足 是退费  并且  经过上面方法计算后的 balance         比较方法
        if (!"DEPOSIT".equals(type) && ledger(admissionId).balance().compareTo(amount)<0){
            throw new IllegalArgumentException("住院账户余额不足");
        }
        //最后转为DTO返回最新流水
        InpatientFeeTransaction transaction = new InpatientFeeTransaction();
        transaction.setAdmissionId(admissionId);
        transaction.setTransactionType(type);
        transaction.setItemName("CHARGE".equals(type)?catalogItem.getItemName():("DEPOSIT").equals(type)?"预交金":"余额退款");
        transaction.setQuantity(quantity);
        transaction.setUnitPrice(unitPrice);
        transaction.setAmount(amount);
        transaction.setNote(request.note());
        transactionMapper.insert(transaction);
        //上面单条流水有计算方式
        return ledger(admissionId);
    }

    //将实体信息转换为接口响应的DTO
    private InpatientFeeTransactionDTO toDto(InpatientFeeTransaction transaction){
        return new InpatientFeeTransactionDTO(
                transaction.getId(),
                transaction.getAdmissionId(),
                transaction.getTransactionType(),
                transaction.getItemName(),
                transaction.getQuantity(),
                transaction.getUnitPrice(),
                transaction.getAmount(),
                transaction.getNote(),
                transaction.getCreatedAt()
        );
    }

    //将金额规范为0,便于后续金额计算
    private BigDecimal zero(BigDecimal value){
        return value == null?BigDecimal.ZERO:value;
    }

    //校验：根据登记id查询住院登记记录
    private InpatientAdmission requireAdmission(Long admissionId){
        InpatientAdmission admission = admissionMapper.selectById(admissionId);
        if(admission == null){
            throw new IllegalArgumentException("住院登记记录不存在!");
        }
        return admission;
    }

}
