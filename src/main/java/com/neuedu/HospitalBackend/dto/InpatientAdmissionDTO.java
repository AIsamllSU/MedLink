package com.neuedu.HospitalBackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InpatientAdmissionDTO {
    private Long id;
    // 文档注释：住院号
    private String inpatientNo;
    // 文档注释：患者病历号
    private String caseNo;
    // 文档注释：患者或医生姓名
    private String name;
    // 文档注释：接口展示的性别
    private String sex;
    // 文档注释：患者出生日期
    private LocalDate birthday;
    // 文档注释：患者年龄
    private Integer age;
    // 文档注释：身份证号码
    private String idCard;
    // 文档注释：联系电话
    private String phone;
    // 文档注释：患者联系地址
    private String address;
    // 文档注释：入院日期
    private LocalDate admissionDate;
    // 文档注释：入院来源
    private String admissionSource;
    // 文档注释：科室名称或科室资料
    private String department;
    // 文档注释：病区名称
    private String ward;
    // 文档注释：床位号
    private String bedNo;
    // 文档注释：医生名称
    private String doctor;
    // 文档注释：患者入院诊断内容。
    private String diagnosis;
    // 文档注释：初始押金金额
    private BigDecimal deposit;
    // 文档注释：业务状态
    private String status;
}
