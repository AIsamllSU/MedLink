package com.neuedu.HospitalBackend.dto;

import lombok.Data;

import java.util.List;

/**
 * 收费项目保存的请求对象
 */
@Data
public class ChargeCreateRequest {
    //1、发票号码
    private String invoiceNo;

    //2、请求记录中的项目集合
    private List<ChargeItemDTO> items;
}
