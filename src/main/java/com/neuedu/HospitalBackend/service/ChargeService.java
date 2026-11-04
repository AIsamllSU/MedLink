package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.ChargeCreateRequest;
import com.neuedu.HospitalBackend.dto.ChargeItemDTO;
import com.neuedu.HospitalBackend.entity.ChargeItem;

import java.util.List;

/**
 * 收费业务接口
 */
public interface ChargeService {
    //1、查询门诊收费明细，把前端页面需要的数据封装返回
    List<ChargeItemDTO> listChargeItems();

    //2、校验收费请求并保存收费项目明细
    List<ChargeItemDTO> saveChargeItems(ChargeCreateRequest request);
}
