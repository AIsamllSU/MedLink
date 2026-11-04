package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 映射收费项目目录，保存收费名称、规格、价格和执行科室等信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("charge_catalog_item")
public class ChargeCatalogItem {
    //主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    //收费项目名称
    private String itemName;
    //收费项目规格
    private String spec;
    //项目单价
    private BigDecimal price;
    //计量单位
    private String unit;
    //科室id
    private Long departmentId;
    //业务状态
    private String status;
    //创建时间
    private LocalDateTime createdAt;
    //注意：这个字段在原表中并不存在，只是实体类中用来接收，展示的额外字段
    @TableField(exist = false)
    private String department;
}
