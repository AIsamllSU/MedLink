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
 * 发票实体保存挂号/退费对应的发票信息，出现在挂号页面和退号页面的明细中
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("invoice")
public class Invoice {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String invoiceNo;//发票号码
    private Long registrationId;//门诊挂号记录ID
    private String invoiceType;//发票类型
    private BigDecimal totalAmount;//总金额
    private BigDecimal cashAmount;//本次现金金额
    private BigDecimal selfPayAmount;//个人自付金额
    private BigDecimal reimburseAmount;//报销金额
    private BigDecimal actualAmount;//实际发生金额
    private BigDecimal diffAmount;//差额
    private String status;//业务状态（已退费/未退费）
    private LocalDateTime createdAt;//创建时间
}
