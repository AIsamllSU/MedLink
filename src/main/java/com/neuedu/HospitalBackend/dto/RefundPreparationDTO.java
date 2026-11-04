package com.neuedu.HospitalBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundPreparationDTO {
    private Long refundOrderId;//退费单ID
    private String invoiceNo;//发票号
    private String invoiceStatus;//发票状态
    // 2026-11-02 新增：退费页面单据头部展示所需的信息。
    private String caseNo;//病历号
    private String patientName;//患者姓名
    private String settleType;//结算类型
    private BigDecimal totalAmount;//总金额
    private BigDecimal cashPay;//现金支付金额
    private BigDecimal accountPay;//账号/医保账户支付
    private BigDecimal realPay;//实际支付
    private BigDecimal reimburseAmount;//报销金额
    /**
     * 退费明细条目集合（每个发票号对应的可退项目）
     *
     * 注意：泛型是明细DTO，这是一个一对多关系，一条收费单对应多条收费项目（药品、检查等）
     * 每条勾选退费
     */
    private List<RefundRecordDTO> items;
}
