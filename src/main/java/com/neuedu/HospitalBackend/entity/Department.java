package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 科室实体：用于存储门诊科室、检查科等基础信息，并作为挂号和收费的基础维度信息支持
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("department")
public class Department {
    //数据库主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    //科室编码
    private String deptCode;
    //科室名称
    private String deptName;
    //科室说明
    private String description;
    //业务状态
    private String status;
    //记录创建时间
    private LocalDateTime createdAt;
}
