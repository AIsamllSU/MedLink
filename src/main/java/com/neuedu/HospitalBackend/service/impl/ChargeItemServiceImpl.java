package com.neuedu.HospitalBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.ChargeCreateRequest;
import com.neuedu.HospitalBackend.dto.ChargeItemDTO;
import com.neuedu.HospitalBackend.entity.ChargeItem;
import com.neuedu.HospitalBackend.entity.Department;
import com.neuedu.HospitalBackend.mapper.ChargeItemMapper;
import com.neuedu.HospitalBackend.mapper.DepartmentMapper;
import com.neuedu.HospitalBackend.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 收费业务实现类
 */
@Service
@RequiredArgsConstructor
public class ChargeItemServiceImpl implements ChargeService {
    //门诊收费Mapper依赖
    private final ChargeItemMapper chargeItemMapper;
    //科室mapper依赖
    private final DepartmentMapper departmentMapper;

    /**
     * 查询门诊收费明细，返回给前端进行数据展示
     * @return
     */
    @Override
    public List<ChargeItemDTO> listChargeItems() {
        //1、查询所有的收费项目通过创建时间倒叙排序
        List<ChargeItem> items = chargeItemMapper.selectList(new QueryWrapper<ChargeItem>().orderByDesc("created_at"));
        //2、把得到的数据结果封装为DTO,返回给前端
        List<ChargeItemDTO> result = new ArrayList<>();
        for(ChargeItem item : items){
            //根据科室ID查询所有科室信息
            Department department = departmentMapper.selectById(item.getDepartmentId());
            //把查询到的结果和上面收费项目一起封装为   DTO 类型返回给前端
            result.add(new ChargeItemDTO(
                    item.getId(),
                    item.getItemName(),
                    item.getSpec(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getUnit(),
                    item.getTimes(),
                    item.getAmount(),
                    //判断科室名称是否为空，为空就直接返回门诊办公室，不为空则返回对应的科室名称
                    department == null?"门诊办公室":department.getDeptName(),
                    //默认不选中
                    item.getSelected() == null?false:item.getSelected()
            ));
        }
        return result;
    }

    // 2026-11-02 新增：批量保存失败时回滚，避免收费明细只写入一部分。
    /**
     * 效验收费请求，并保存收费项目明细
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ChargeItemDTO> saveChargeItems(ChargeCreateRequest request) {
        //1、判断请求是否为空且请求参数是否为空且请求参数不为空集
        if(request==null||request.getItems()==null||request.getItems().isEmpty()){
            return List.of();//返回空集合/空结果，避免前端请求为null
        }
        //2、创建集合
        List<ChargeItemDTO> result = new ArrayList<>();
        //3、遍历集合处理数据，最后将数据封装为DTO类型返回给前端
        for(ChargeItemDTO item:request.getItems()){
            ChargeItem entity = new ChargeItem();
            entity.setId(item.getId());
            entity.setRegistrationId(1L);
            entity.setItemName(item.getName());
            entity.setSpec(item.getSpec());
            //保存金额时,为空的话，直接保存为0，避免报错
            entity.setPrice(item.getPrice()==null? BigDecimal.ZERO:item.getPrice());
            entity.setQuantity(item.getQuantity()==null?1: item.getQuantity());
            entity.setUnit(item.getUnit());
            entity.setTimes(item.getTimes()==null?1:item.getTimes());
            //交易金额
            BigDecimal amount = entity.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity()))
                    .multiply(BigDecimal.valueOf(entity.getTimes()));
            entity.setAmount(amount);
            entity.setSelected(Boolean.TRUE.equals(item.getSelected()));
            //科室信息
            Department department = item.getDepartment()==null?null:departmentMapper.selectOne(
                    new QueryWrapper<Department>().eq("dept_name",item.getDepartment())
            );
            entity.setDepartmentId(department==null?1L:department.getId());

            // 2026-11-02 修正：已有明细按主键更新，只有新项目才插入，避免重复收费。
            if(item.getId()==null){
                if(chargeItemMapper.insert(entity) != 1){
                    throw new IllegalStateException("收费项目新增失败");
                }
            }else{
                if(chargeItemMapper.updateById(entity) != 1){
                    throw new IllegalArgumentException("收费明细不存在或已被删除，请刷新后重试");
                }
            }

            result.add(new ChargeItemDTO(
                    entity.getId(),
                    entity.getItemName(),
                    item.getSpec(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getUnit(),
                    item.getTimes(),
                    item.getAmount(),
                    department == null?"门诊办公室":department.getDeptName(),
                    entity.getSelected()
            ));
        }
        return result;
    }
}
