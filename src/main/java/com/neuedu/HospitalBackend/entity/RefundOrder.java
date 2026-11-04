package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退费单实体：对应门诊退费页面的单据信息，用于汇总退费金额及退费状态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("refund_order")
public class RefundOrder {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//主键
    private String invoiceNo;//发票号码
    private Long patientId;//关联患者记录的主键ID
    private BigDecimal totalRefundAmount;//累计退费金额
    private String refundReason;//退费原因
    private String status;//退费状态
    private LocalDateTime createdAt;//创建时间
}
