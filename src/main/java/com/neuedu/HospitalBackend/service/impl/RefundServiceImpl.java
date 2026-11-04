package com.neuedu.HospitalBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.RefundPreparationDTO;
import com.neuedu.HospitalBackend.dto.RefundRecordDTO;
import com.neuedu.HospitalBackend.dto.RefundRequest;
import com.neuedu.HospitalBackend.entity.ChargeItem;
import com.neuedu.HospitalBackend.entity.Invoice;
import com.neuedu.HospitalBackend.entity.Patient;
import com.neuedu.HospitalBackend.entity.RefundItem;
import com.neuedu.HospitalBackend.entity.RefundOrder;
import com.neuedu.HospitalBackend.entity.Registration;
import com.neuedu.HospitalBackend.mapper.ChargeItemMapper;
import com.neuedu.HospitalBackend.mapper.InvoiceMapper;
import com.neuedu.HospitalBackend.mapper.PatientMapper;
import com.neuedu.HospitalBackend.mapper.RefundItemMapper;
import com.neuedu.HospitalBackend.mapper.RefundOrderMapper;
import com.neuedu.HospitalBackend.mapper.RegistrationMapper;
import com.neuedu.HospitalBackend.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    //退费页面显示的信息，一般不做修改，因此定义成常量
    private static final String PAID = "已收费";
    private static final String PARTIALLY_REFUNDED = "部分退费";
    private static final String REFUNDED = "已退费";
    private static final String REFUND_PENDING = "待退费";
    //退费持久层接口依赖
    private final RefundItemMapper refundItemMapper;
    //发票信息持久层依赖
    private final InvoiceMapper invoiceMapper;
    //退费订单持久层依赖
    private final RefundOrderMapper refundOrderMapper;
    //挂号持久层依赖
    private final RegistrationMapper registrationMapper;
    //门诊收费明细
    private final ChargeItemMapper chargeItemMapper;
    //患者信息
    private final PatientMapper patientMapper;

    /**
     * 根据发票号 获取退费的信息
     * @param invoiceNo
     * @return
     */
    @Override
    @Transactional
    public RefundPreparationDTO prepareRefund(String invoiceNo) {
        //校验：发票号为空则直接抛出异常
        if (invoiceNo == null || invoiceNo.isBlank()) {
            throw new IllegalArgumentException("发票编号不能为空");
        }
        //lockInvoice：锁定这条发票记录，悲观锁！！防止多人同时对同一张发票发起退费，并发安全
        Invoice invoice = lockInvoice(invoiceNo.trim());
        System.out.println(invoice+"000000000000000000");
        //效验方法：效验这张发票能不能退
        validateRefundableInvoice(invoice);
        //根据发票中的挂号ID，查询挂号几率
        Registration registration = registrationMapper.selectById(invoice.getRegistrationId());
        if (registration == null) {
            throw new IllegalArgumentException("发票关联的挂号记录不存在");
        }
        //通过挂号记录中的患者id，查询患者信息
        Patient patient = patientMapper.selectById(registration.getPatientId());
        if (patient == null) {
            throw new IllegalArgumentException("发票关联的患者记录不存在");
        }
        //查询本次挂号下所有的收费项目
        List<ChargeItem> charges = chargeItemMapper.selectList(
                new QueryWrapper<ChargeItem>().eq("registration_id", registration.getId()).orderByAsc("id"));
        //把收费实体转换为前端退费明细DTO
        List<RefundRecordDTO> items = refundableItems(charges);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("该发票没有可退费的收费项目");
        }
        //核心：准备阶段就插入退费单，状态：【待确认】
        RefundOrder order = new RefundOrder();
        order.setInvoiceNo(invoice.getInvoiceNo());
        order.setPatientId(registration.getPatientId());
        order.setTotalRefundAmount(BigDecimal.ZERO.setScale(2));//暂时退费总金额为0，前端勾选提交后在更新
        order.setStatus(REFUND_PENDING);//待确认
        if (refundOrderMapper.insert(order) != 1 || order.getId() == null) {
            throw new IllegalStateException("创建退费主单失败");
        }
        // 2026-11-02 新增：返回退费单据头部所需的患者、结算及支付信息。
        return new RefundPreparationDTO(
                order.getId(),//把退费订单ID放进RefundPreparationDTO的refundOrderId,返回给前端，前端提交时带上这个refundOrderId,更新退费单
                invoice.getInvoiceNo(),
                invoice.getStatus(),
                patient.getCaseNo(),
                patient.getName(),
                registration.getSettleType(),
                zeroIfNull(invoice.getTotalAmount()),
                zeroIfNull(invoice.getCashAmount()),
                zeroIfNull(invoice.getSelfPayAmount()),
                zeroIfNull(invoice.getActualAmount()),
                zeroIfNull(invoice.getReimburseAmount()),
                items
        );
    }

    /**
     * 封装所有退费明细为DTO返回给前端
     * @return
     */
    @Override
    public List<RefundRecordDTO> listRefundRecords() {
        //根据RefundItem退费明细表，查询已经提交成功入库的退费明细
        List<RefundItem> items = refundItemMapper.selectList(
                new QueryWrapper<RefundItem>().orderByDesc("created_at"));
        List<RefundRecordDTO> result = new ArrayList<>();
        for (RefundItem item : items) {
            result.add(new RefundRecordDTO(
                    item.getItemName(),
                    item.getSpec(),
                    item.getPriceBefore(),
                    item.getPriceAfter(),
                    item.getOpenedQty(),
                    item.getUnit(),
                    item.getRefundableQty(),
                    item.getRefundQty(),
                    item.getRefundAmount(),
                    item.getDiscountRefundAmount(),
                    item.getMedicineFlag(),
                    false,
                    item.getChargeItemId()
            ));
        }
        return result;
    }

    @Override
    @Transactional
    public List<RefundRecordDTO> refund(RefundRequest request) {
        //基础校验
        if (request == null || request.getRefundOrderId() == null) {
            throw new IllegalArgumentException("退费主单编号不能为空");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("请选择需要退费的项目");
        }
        //查询退费单，校验退费主单状态
        RefundOrder order = refundOrderMapper.selectOne(
                new QueryWrapper<RefundOrder>().eq("id", request.getRefundOrderId()).last("FOR UPDATE"));
        if (order == null) {
            throw new IllegalArgumentException("退费主单不存在");
        }
        //注意：只允许【待提交】状态的工单执行退费，防止重复提交
        if (!REFUND_PENDING.equals(order.getStatus())) {
            throw new IllegalArgumentException("该退费主单已处理，不能重复提交");
        }
        //悲观锁锁住原始发票！防止多人同时对一张发票退费处理并发问题
        Invoice invoice = lockInvoice(order.getInvoiceNo());
        validateRefundableInvoice(invoice);//复用之前的效验逻辑
        //拿到挂号信息，查询本次挂号信息下全部的收费项目
        Registration registration = registrationMapper.selectById(invoice.getRegistrationId());
        if (registration == null) {
            throw new IllegalArgumentException("发票关联的挂号记录不存在");
        }
        List<ChargeItem> charges = chargeItemMapper.selectList(
                new QueryWrapper<ChargeItem>().eq("registration_id", registration.getId()).orderByAsc("id"));
        //放进map集合中，方便后续跟进chargeItemId快速取出收费项目
        Map<Long, ChargeItem> chargeById = new HashMap<>();
        for (ChargeItem charge : charges) {
            chargeById.put(charge.getId(), charge);
        }
        //查询历史已经退过的数量
        Map<Long, Integer> refundedQuantities = refundedQuantities(charges);
        //数据装到Set集合中（set集合无序不重复）：本次使用业务：防止前端恶意或bug，同一个收费项目退费两次
        Set<Long> submittedIds = new HashSet<>();
        BigDecimal totalRefundAmount = BigDecimal.ZERO.setScale(2);//10.25
        List<RefundRecordDTO> result = new ArrayList<>();
        for (RefundRecordDTO requested : request.getItems()) {
            if (requested == null || requested.getChargeItemId() == null) {
                throw new IllegalArgumentException("退费明细缺少收费项目编号");
            }
            Long chargeId = requested.getChargeItemId();
            //判断集合中有没有这个id，没有就true，添加，有就false，抛出异常
            if (!submittedIds.add(chargeId)) {
                throw new IllegalArgumentException("退费明细中存在重复收费项目");
            }
            //根据id查询退费项目，没有此id证明为空抛出异常
            ChargeItem charge = chargeById.get(chargeId);
            if (charge == null) {
                throw new IllegalArgumentException("退费项目不属于当前发票");
            }
            int openedQty = openedQuantity(charge);//总量：单次数量*次数
            //历史累计已退
            int alreadyRefunded = refundedQuantities.getOrDefault(chargeId, 0);
            //实时剩余可退
            int remainingQty = openedQty - alreadyRefunded;
            //注意：这个地方是数据库事务重新查库算余量，不要使用前端传递过来的数据
            Integer refundQty = requested.getRefundQty();
            if (refundQty == null || refundQty <= 0) {
                throw new IllegalArgumentException(charge.getItemName() + "退费数量必须大于0");
            }
            if (refundQty > remainingQty) {
                throw new IllegalArgumentException(charge.getItemName() + "本次退费数量不能大于可退数量");
            }

            BigDecimal price = zeroIfNull(charge.getPrice());
            BigDecimal refundAmount = price.multiply(BigDecimal.valueOf(refundQty))
                    .setScale(2, RoundingMode.HALF_UP);
            RefundItem refundItem = new RefundItem();
            refundItem.setRefundOrderId(order.getId());
            refundItem.setChargeItemId(chargeId);
            refundItem.setItemName(charge.getItemName());
            refundItem.setSpec(charge.getSpec());
            refundItem.setPriceBefore(price);
            refundItem.setPriceAfter(price);
            refundItem.setOpenedQty(openedQty);
            refundItem.setUnit(charge.getUnit());
            refundItem.setRefundableQty(remainingQty);
            refundItem.setRefundQty(refundQty);
            refundItem.setRefundAmount(refundAmount);
            refundItem.setDiscountRefundAmount(refundAmount);
            refundItem.setMedicineFlag("未发药");
            if (refundItemMapper.insert(refundItem) != 1) {
                throw new IllegalStateException("保存退费明细失败");
            }

            totalRefundAmount = totalRefundAmount.add(refundAmount);
            refundedQuantities.put(chargeId, alreadyRefunded + refundQty);
            result.add(toRefundRecord(charge, openedQty, remainingQty, refundQty, refundAmount));
        }

        order.setTotalRefundAmount(totalRefundAmount);
        order.setStatus(REFUNDED);
        if (refundOrderMapper.updateById(order) != 1) {
            throw new IllegalStateException("更新退费主单失败");
        }

        boolean fullyRefunded = !charges.isEmpty();
        for (ChargeItem charge : charges) {
            if (refundedQuantities.getOrDefault(charge.getId(), 0) < openedQuantity(charge)) {
                fullyRefunded = false;
                break;
            }
        }
        invoice.setStatus(fullyRefunded ? REFUNDED : PARTIALLY_REFUNDED);
        if (invoiceMapper.updateById(invoice) != 1) {
            throw new IllegalStateException("更新发票状态失败");
        }
        return result;
    }

    //私有方法，仅供本类使用。
    //悲观锁，锁定发票，防止多人同时操作同一张退费单
    private Invoice lockInvoice(String invoiceNo) {
        Invoice invoice = invoiceMapper.selectOne(
                new QueryWrapper<Invoice>().eq("invoice_no", invoiceNo).last("FOR UPDATE"));
        if (invoice == null) {
            throw new IllegalArgumentException("未找到对应的发票");
        }
        return invoice;
    }
    //校验发票能不能退费
    private void validateRefundableInvoice(Invoice invoice) {
        if (REFUNDED.equals(invoice.getStatus())) {
            throw new IllegalArgumentException("该发票已退费，不能重复退费");
        }
        if (!PAID.equals(invoice.getStatus()) && !PARTIALLY_REFUNDED.equals(invoice.getStatus())) {
            System.out.println(invoice.getStatus()+"=================");
            throw new IllegalArgumentException("只有已收费或部分退费的发票可以退费");
        }
    }
    //把收费项目列表，转换成前端展示的退费明细DTO
    private List<RefundRecordDTO> refundableItems(List<ChargeItem> charges) {
        //查询项目中【已经推过的数量】
        Map<Long, Integer> refundedQuantities = refundedQuantities(charges);
        List<RefundRecordDTO> result = new ArrayList<>();
        for (ChargeItem charge : charges) {
            int openedQty = openedQuantity(charge);//项目中原始的订单数量
            //原始数量 - 已经退过的数量 = 剩余可退数量
            int remainingQty = openedQty - refundedQuantities.getOrDefault(charge.getId(), 0);
            //大于0，表示当前挂号下，还有可退费项目，已经全部退完的，不在显示，不能再退
            if (remainingQty > 0) {
                result.add(toRefundRecord(charge, openedQty, remainingQty, 0, BigDecimal.ZERO.setScale(2)));
            }
        }
        return result;
    }
    //查询收费项目中，历史已经退掉的数量
    private Map<Long, Integer> refundedQuantities(List<ChargeItem> charges) {
        Map<Long, Integer> result = new HashMap<>();
        if (charges.isEmpty()) {
            return result;
        }
        //取出所有收费项目id
        List<Long> chargeIds = charges.stream().map(ChargeItem::getId).toList();
        //查询所有charge_item_id对应的退费明细
        List<RefundItem> refundItems = refundItemMapper.selectList(
                new QueryWrapper<RefundItem>().in("charge_item_id", chargeIds));
        for (RefundItem refundItem : refundItems) {
            //merge：累加和 同一个发票多次退费，数量累加求和
            result.merge(refundItem.getChargeItemId(), refundItem.getRefundQty() == null ? 0 : refundItem.getRefundQty(),
                    Integer::sum);
        }
        return result;
    }
    //计算项目中原始挂号下所有的开单数量
    private int openedQuantity(ChargeItem charge) {
        //项目中单次数量 null默认 0
        int quantity = charge.getQuantity() == null ? 0 : charge.getQuantity();
        //次数，null默认1次 例如：理疗做几次
        int times = charge.getTimes() == null ? 1 : charge.getTimes();
        //总数量 = 单次数量* 次数 ，防止负数 最小取0
        return Math.max(quantity * times, 0);
    }
    //ChargeItem实体 转换RefundRecordDTO
    private RefundRecordDTO toRefundRecord(ChargeItem charge, int openedQty, int remainingQty,
                                           int refundQty, BigDecimal refundAmount) {
        BigDecimal price = zeroIfNull(charge.getPrice());
        return new RefundRecordDTO(
                charge.getItemName(),
                charge.getSpec(),
                price,
                price,
                openedQty,
                charge.getUnit(),
                remainingQty,
                refundQty,
                refundAmount,
                refundAmount,
                "未发药",
                false,//复选框中，默认不勾选
                charge.getId()
        );
    }
    //工具方法：金额类型BigDecimal 不能直接运算，且需要判空，防止空指针！
    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
