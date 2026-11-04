package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者信息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("patient")
public class Patient {
    //主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    //病历号
    private String caseNo;
    //患者姓名
    private String name;
    //性别
    private String gender;
    //身份证号
    private String idCard;
    //出生日期
    private LocalDate birthday;
    //年龄
    private Integer age;
    //就诊卡号
    private String medicalCard;
    //联系地址
    private String address;
    //结算方式
    private String settleType;
    //医保类别
    private String medicalType;
    //患者类别
    private String patientType;
    //创建时间
    private LocalDateTime createdAt;
}
