package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.dto.RefundPreparationDTO;
import com.neuedu.HospitalBackend.dto.RefundRecordDTO;
import com.neuedu.HospitalBackend.service.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RefundControllerTest {
    @Mock
    private RefundService refundService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RefundController(refundService)).build();
    }

    @Test
    void listWithInvoiceNumberReturnsPreparationDataNeededByRefundPage() throws Exception {
        RefundRecordDTO item = new RefundRecordDTO();
        item.setChargeItemId(8L);
        item.setRefundable(2);
        RefundPreparationDTO preparation = new RefundPreparationDTO();
        preparation.setRefundOrderId(42L);
        preparation.setInvoiceNo("INV-1");
        preparation.setInvoiceStatus("已收费");
        preparation.setCaseNo("10001");
        preparation.setPatientName("测试患者");
        preparation.setSettleType("自费");
        preparation.setTotalAmount(new java.math.BigDecimal("100.00"));
        preparation.setCashPay(new java.math.BigDecimal("60.00"));
        preparation.setAccountPay(new java.math.BigDecimal("20.00"));
        preparation.setRealPay(new java.math.BigDecimal("80.00"));
        preparation.setReimburseAmount(new java.math.BigDecimal("20.00"));
        preparation.setItems(List.of(item));
        when(refundService.prepareRefund("INV-1")).thenReturn(preparation);

        mockMvc.perform(get("/api/refund/list").param("invoiceNo", "INV-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.refundOrderId").value(42))
                .andExpect(jsonPath("$.data.invoiceNo").value("INV-1"))
                .andExpect(jsonPath("$.data.invoiceStatus").value("已收费"))
                .andExpect(jsonPath("$.data.caseNo").value("10001"))
                .andExpect(jsonPath("$.data.patientName").value("测试患者"))
                .andExpect(jsonPath("$.data.settleType").value("自费"))
                .andExpect(jsonPath("$.data.totalAmount").value(100.00))
                .andExpect(jsonPath("$.data.cashPay").value(60.00))
                .andExpect(jsonPath("$.data.accountPay").value(20.00))
                .andExpect(jsonPath("$.data.realPay").value(80.00))
                .andExpect(jsonPath("$.data.reimburseAmount").value(20.00))
                .andExpect(jsonPath("$.data.items[0].chargeItemId").value(8))
                .andExpect(jsonPath("$.data.items[0].refundable").value(2));

        verify(refundService).prepareRefund("INV-1");
    }

    @Test
    void listWithoutInvoiceNumberKeepsExistingRefundRecordListResponse() throws Exception {
        RefundRecordDTO item = new RefundRecordDTO();
        item.setName("检查项目");
        when(refundService.listRefundRecords()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/refund/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("检查项目"))
                .andExpect(jsonPath("$.data[0].refundOrderId").doesNotExist());

        verify(refundService).listRefundRecords();
    }
}
