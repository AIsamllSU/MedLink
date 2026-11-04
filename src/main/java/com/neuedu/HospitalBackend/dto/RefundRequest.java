package com.neuedu.HospitalBackend.dto;

import lombok.Data;

import java.util.List;

/**
 * 接收门诊退费单以及退费项目请求
 */
@Data
public class RefundRequest {
    // 退费主单 ID，由按发票查询接口创建并返回
    private Long refundOrderId;
    //发票号码
    private String invoiceNo;
    //关联患者记录的主键ID
    private String patientId;
    //封装好的DTO对象
    private List<RefundRecordDTO> items;
}
