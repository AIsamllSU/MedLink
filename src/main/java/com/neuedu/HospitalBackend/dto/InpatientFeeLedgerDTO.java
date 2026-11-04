package com.neuedu.HospitalBackend.dto;

import com.neuedu.HospitalBackend.entity.InpatientFeeTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * 向前端返回住院账户余额汇总和流水台账
 *
 */
public record InpatientFeeLedgerDTO(
        Long admissionId,//住院登记ID
        String inpatientNo,//住院号
        String caseNo,//病历号
        String name,//患者姓名
        BigDecimal initialDeposit,//初识预交押金（入院第一次押金）
        BigDecimal totalCharges,//累计总费用（所有计费项目总和）
        BigDecimal totalPayments,//累计缴费（所有押金存入总和，含补缴）
        BigDecimal totalRefunds,//累计退款（退押金、退费总和）
        BigDecimal balance,//当前余额（累计缴费-累计退费-总费用）
        List<InpatientFeeTransactionDTO> transactions//流水明细（押金/费用流水）
) {
}
