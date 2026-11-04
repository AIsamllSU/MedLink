package com.neuedu.HospitalBackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 接收门诊挂号创建所需要的患者和就诊信息
 */
@Data
public class RegistrationCreateRequest {
    // 文档注释：患者类别
    private String patientType;
    // 文档注释：身份证号码
    private String idCard;
    // 文档注释：患者病历号
    private String caseNo;
    // 文档注释：结算方式
    private String settleType;
    // 文档注释：就诊日期
    private LocalDate visitDate;
    // 文档注释：患者或医生姓名
    private String name;
    // 文档注释：就诊卡号
    private String medicalCard;
    // 文档注释：医生职称或挂号级别
    private String drLevel;
    // 文档注释：接口展示的性别
    private String sex;
    // 文档注释：医保类别
    private String medicalType;
    // 文档注释：科室名称或科室资料
    private String department;
    // 文档注释：患者年龄
    private Integer age;
    // 文档注释：年龄单位
    private String ageUnit;
    // 文档注释：患者出生日期
    private LocalDate birthday;
    // 文档注释：患者联系地址
    private String address;
    // 文档注释：医生名称
    private String doctor;
    // 文档注释：挂号实收金额
    private BigDecimal regPay;
}
