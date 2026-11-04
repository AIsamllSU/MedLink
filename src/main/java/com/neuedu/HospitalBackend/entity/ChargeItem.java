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
 * 收费项目的实体
 * 对应门诊划价收费页面的清单，可作为付款、退费、发票明细的数据来源
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("charge_item")
public class ChargeItem {
    @TableId(value = "id", type = IdType.AUTO)
    // 文档注释：数据库主键
    private Long id;

    // 文档注释：需要退号的挂号记录 ID。
    private Long registrationId;

    // 文档注释：收费项目名称
    private String itemName;

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

    // 文档注释：所属科室 ID
    private Long departmentId;

    // 文档注释：项目是否被选中
    private Boolean selected;

    // 文档注释：记录创建时间
    private LocalDateTime createdAt;
}
