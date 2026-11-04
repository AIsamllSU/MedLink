package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 医生实体：用于保存医生的档案信息，支撑挂号页面中的医生选择和门诊业务流转
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("doctor")
public class Doctor {
    //数据库主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    //科室ID
    private Long departmentId;
    //医生编号
    private String doctorCode;
    //医生名称
    private String name;
    //医生职称
    private String title;
    //医生电话
    private String phone;
    //业务状态
    private String status;
    //创建时间
    private LocalDateTime createdAt;
}
