package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录实体：映射挂号记录、保存患者、科室、医生和挂号状态等信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("registration")
public class Registration {
    //主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    //关联患者记录的主键ID
    private Long patientId;


    //发票号码
    private String invoiceNo;
    //所属科室ID
    private Long departmentId;
    //接诊医生ID
    private Long doctorId;
    //挂号级别
    private String regLevel;
    //挂号日期
    private LocalDate regDate;
    //就诊日期
    private LocalDate visitDate;
    //业务来源
    private String source;
    //就诊状态
    private String diagnoseStatus;
    //业务状态
    private String status;
    //收费金额
    private BigDecimal amount;
    //结算方式
    private String settleType;
    //创建时间
    private LocalDateTime createdAt;
}
