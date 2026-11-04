package com.neuedu.HospitalBackend.dto;

import java.math.BigDecimal;

/**
 * 接收住院押金，费用或退款流水请求
 */
public record InpatientFeeRequest(
        String transactionType,//流水类型
        Long catalogItemId,//收费项目id
        Integer quantity,//数量
        BigDecimal amount,//金额
        String note//备注
) {
}
