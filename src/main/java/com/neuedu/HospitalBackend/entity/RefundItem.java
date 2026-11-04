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
 * 退费明细实体
 *
 * 对应退费页面中每一个项目明细，用于计算不同项目的退费数量和退款金额
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("refund_item")
public class RefundItem {
    @TableId(value = "id", type = IdType.AUTO)
    // 文档注释：数据库主键
    private Long id;

    // 文档注释：关联退费单 ID
    private Long refundOrderId;

    // 文档注释：关联收费明细 ID
    private Long chargeItemId;

    // 文档注释：收费项目名称
    private String itemName;

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

    // 文档注释：剩余可退数量
    private Integer refundableQty;

    // 文档注释：本次退费数量
    private Integer refundQty;

    // 文档注释：本次退费金额
    private BigDecimal refundAmount;

    // 文档注释：折后退费金额
    private BigDecimal discountRefundAmount;

    // 文档注释：是否为药品项目
    private String medicineFlag;

    // 文档注释：记录创建时间
    private LocalDateTime createdAt;
}
