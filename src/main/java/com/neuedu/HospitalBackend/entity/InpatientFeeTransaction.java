package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("inpatient_fee_transaction")
public class InpatientFeeTransaction {
    @TableId(type = IdType.AUTO)
    // 文档注释：数据库主键
    private Long id;
    // 文档注释：住院登记 ID
    private Long admissionId;
    // 文档注释：住院流水类型
    private String transactionType;
    // 文档注释：收费项目名称
    private String itemName;
    // 文档注释：项目数量
    private Integer quantity;
    // 文档注释：单次费用单价
    private BigDecimal unitPrice;
    // 文档注释：交易金额或收费金额
    private BigDecimal amount;
    // 文档注释：补充说明
    private String note;
    // 文档注释：记录创建时间
    private LocalDateTime createdAt;
}
