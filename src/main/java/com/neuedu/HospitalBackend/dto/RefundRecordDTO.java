package com.neuedu.HospitalBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 退费页面返回的对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundRecordDTO {
    // 文档注释：患者或医生姓名
    private String name;
    // 文档注释：收费项目规格
    private String spec;
    // 文档注释：退费前价格
    private BigDecimal priceBefore;
    // 文档注释：折后价格
    private BigDecimal priceAfter;
    // 文档注释：原始开单数量
    private Integer openedQty;
    // 文档注释：计量单位
    private String unit;
    // 文档注释：当前可退数量
    private Integer refundable;
    // 文档注释：本次退费数量
    private Integer refundQty;
    // 文档注释：本次退费金额
    private BigDecimal refundAmount;
    // 文档注释：折后退费金额
    private BigDecimal discountRefundAmount;
    // 文档注释：是否为药品项目
    private String medicineFlag;
    // 文档注释：项目是否被选中
    private Boolean selected;
    // 收费明细 ID，用于提交退费数量
    private Long chargeItemId;
}
