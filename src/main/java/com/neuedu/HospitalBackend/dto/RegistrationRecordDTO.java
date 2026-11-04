package com.neuedu.HospitalBackend.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号页面返回的对象
 *
 * 主要用来封装前端需要展示的挂号列表数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRecordDTO {
    //患者病历号
    private String caseNo;
    //患者或医生姓名
    private String name;
    //接口中展示的性别
    private String sex;
    //患者出生日期
    private LocalDate birthday;
    //患者身份证号码
    private String pid;
    //挂号发票号码
    private String regInvoice;
    //结算方式
    private String settleType;
    //挂号级别
    private String regLevel;
    //挂号日期
    private LocalDate regDate;
    //就诊日期
    private LocalDate visitDate;
    //是否已经诊断
    private String diagnosed;
    //业务状态
    private String status;
    //实际支付金额
    private BigDecimal realPay;
    //科室信息
    private String department;
}
