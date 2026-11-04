package com.neuedu.HospitalBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 接收收费项目目录更新或修改请求，组件：收费项目名称，收费规格，项目单价，计量单位，所属科室，业务状态
 */

/**
 *
 * @Data
 * @AllArgsConstructor
 * @NoArgsConstructor
 * public class ChargeCatalogDTO{
 *     String itemName;
 *     String spec;
 *     BigDecimal price;
 *     String unit;
 *     Long departmentId;
 *     String status;
 * }
 *
 *
 */
public record ChargeCatalogRequest(String itemName, String spec, BigDecimal price,String unit,Long departmentId,String status) {
}

