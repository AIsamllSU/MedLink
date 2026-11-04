package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.RefundRecordDTO;
import com.neuedu.HospitalBackend.dto.RefundPreparationDTO;
import com.neuedu.HospitalBackend.dto.RefundRequest;

import java.util.List;

/**
 * 退费业务，业务层接口
 */
public interface RefundService {
    RefundPreparationDTO prepareRefund(String invoiceNo);

    //1、查询门诊退费明细 转换为对应的DTO实体影响给前端
    List<RefundRecordDTO> listRefundRecords();

    //2、提交退费请求并保存退费明细
    List<RefundRecordDTO> refund(RefundRequest request);
}
