package com.neuedu.HospitalBackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 向前端返回的单笔住院流水费用
 *
 * 当前流水查出所有流水 封装成InpatientFeeTransactionDTO塞到InpatientFeeLedgerDTO的transactions里返回给前端
 */
public record InpatientFeeTransactionDTO(
        Long id,//流水ID
        Long admissionId,//住院登记ID
        String transactionType,//流水类型：用来区分这条记录是什么业务
        String itemName,//项目名称，例如 床位房、预交押金等
        Integer quantity,//数量：药品、检查次数等
        BigDecimal unitPrice,//单价：押金流水单价可以为空
        BigDecimal amount,//本条流水金额
        String note,//备注：操作说明或医嘱信息等
        LocalDateTime createdAt//创建时间：流水生成时间
) {
}
