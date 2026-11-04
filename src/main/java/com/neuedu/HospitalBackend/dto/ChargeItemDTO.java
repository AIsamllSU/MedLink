package com.neuedu.HospitalBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChargeItemDTO {
    private Long id;
    // 文档注释：患者或医生姓名
    private String name;
    // 文档注释：收费项目规格
    private String spec;
    // 文档注释：收费项目单价
    private BigDecimal price;
    // 文档注释：项目数量
    private Integer quantity;
    // 文档注释：计量单位
    private String unit;
    // 文档注释：收费次数
    private Integer times;
    // 文档注释：交易金额或收费金额
    private BigDecimal amount;
    // 文档注释：科室名称或科室资料
    private String department;
    // 文档注释：项目是否被选中
    private Boolean selected;
}
