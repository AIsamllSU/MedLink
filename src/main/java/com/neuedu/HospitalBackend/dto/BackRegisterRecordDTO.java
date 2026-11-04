package com.neuedu.HospitalBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 退号页面返回的对象
 * 向前端返回退号页面的使用的挂号记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackRegisterRecordDTO {
    // 文档注释：数据库主键
    private Long id;
    // 文档注释：患者病历号
    private String caseNo;
    // 文档注释：患者或医生姓名
    private String name;
    // 文档注释：接口展示的性别
    private String sex;
    // 文档注释：患者出生日期
    private LocalDate birthday;
    private Integer age;
    // 文档注释：患者身份证号码。
    private String pid;
    // 文档注释：挂号发票号码
    private String regInvoice;
    // 文档注释：结算方式
    private String settleType;
    // 文档注释：挂号级别
    private String regLevel;
    // 文档注释：挂号日期
    private LocalDate regDate;
    // 文档注释：就诊日期
    private LocalDate visitDate;
    // 文档注释：是否已诊断
    private String diagnosed;
    // 文档注释：业务状态
    private String status;
    // 文档注释：实际支付金额
    private BigDecimal realPay;
    // 文档注释：科室名称或科室资料
    private String department;
}
