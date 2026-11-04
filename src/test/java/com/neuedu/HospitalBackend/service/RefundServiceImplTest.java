package com.neuedu.HospitalBackend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.neuedu.HospitalBackend.dto.RefundRecordDTO;
import com.neuedu.HospitalBackend.dto.RefundRequest;
import com.neuedu.HospitalBackend.entity.ChargeItem;
import com.neuedu.HospitalBackend.entity.Invoice;
import com.neuedu.HospitalBackend.entity.Patient;
import com.neuedu.HospitalBackend.entity.RefundItem;
import com.neuedu.HospitalBackend.entity.RefundOrder;
import com.neuedu.HospitalBackend.entity.Registration;
import com.neuedu.HospitalBackend.mapper.ChargeItemMapper;
import com.neuedu.HospitalBackend.mapper.InvoiceMapper;
import com.neuedu.HospitalBackend.mapper.PatientMapper;
import com.neuedu.HospitalBackend.mapper.RefundItemMapper;
import com.neuedu.HospitalBackend.mapper.RefundOrderMapper;
import com.neuedu.HospitalBackend.mapper.RegistrationMapper;
import com.neuedu.HospitalBackend.service.impl.RefundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {
    @Mock private RefundItemMapper refundItemMapper;
    @Mock private InvoiceMapper invoiceMapper;
    @Mock private RefundOrderMapper refundOrderMapper;
    @Mock private RegistrationMapper registrationMapper;
    @Mock private ChargeItemMapper chargeItemMapper;
    @Mock private PatientMapper patientMapper;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundService = new RefundServiceImpl(refundItemMapper, invoiceMapper, refundOrderMapper,
                registrationMapper, chargeItemMapper, patientMapper);
    }

    @Test
    void prepareRefundCreatesOrderAndReturnsAvailableChargeItems() {
        Invoice invoice = invoice("已收费");
        Registration registration = registration();
        ChargeItem charge = chargeItem(8L, 3, 1, "10.00");
        when(invoiceMapper.selectOne(any(Wrapper.class))).thenReturn(invoice);
        when(registrationMapper.selectById(5L)).thenReturn(registration);
        when(patientMapper.selectById(6L)).thenReturn(patient());
        when(chargeItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of(charge));
        when(refundItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of());
        when(refundOrderMapper.insert(any(RefundOrder.class))).thenAnswer(invocation -> {
            ((RefundOrder) invocation.getArgument(0)).setId(42L);
            return 1;
        });

        var result = refundService.prepareRefund("INV-1");

        assertEquals(42L, result.getRefundOrderId());
        assertEquals("INV-1", result.getInvoiceNo());
        assertEquals("10001", result.getCaseNo());
        assertEquals("测试患者", result.getPatientName());
        assertEquals("医保", result.getSettleType());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(new BigDecimal("60.00"), result.getCashPay());
        assertEquals(new BigDecimal("20.00"), result.getAccountPay());
        assertEquals(new BigDecimal("80.00"), result.getRealPay());
        assertEquals(new BigDecimal("20.00"), result.getReimburseAmount());
        assertEquals(3, result.getItems().get(0).getRefundable());
        assertEquals(8L, result.getItems().get(0).getChargeItemId());
        ArgumentCaptor<RefundOrder> orderCaptor = ArgumentCaptor.forClass(RefundOrder.class);
        verify(refundOrderMapper).insert(orderCaptor.capture());
        RefundOrder createdOrder = orderCaptor.getValue();
        assertEquals("INV-1", createdOrder.getInvoiceNo());
        assertEquals(6L, createdOrder.getPatientId());
        assertEquals(new BigDecimal("0.00"), createdOrder.getTotalRefundAmount());
        assertEquals("待退费", createdOrder.getStatus());
    }

    @Test
    void prepareRefundRejectsAlreadyRefundedInvoiceWithoutCreatingOrder() {
        when(invoiceMapper.selectOne(any(Wrapper.class))).thenReturn(invoice("已退费"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> refundService.prepareRefund("INV-1"));

        assertEquals("该发票已退费，不能重复退费", exception.getMessage());
        verify(refundOrderMapper, never()).insert(any(RefundOrder.class));
    }

    @Test
    void refundPersistsServerCalculatedAmountAndMarksInvoicePartiallyRefunded() {
        Invoice invoice = invoice("已收费");
        Registration registration = registration();
        RefundOrder order = new RefundOrder();
        order.setId(42L);
        order.setInvoiceNo("INV-1");
        order.setStatus("待退费");
        ChargeItem charge = chargeItem(8L, 3, 1, "10.00");
        RefundRecordDTO requested = new RefundRecordDTO();
        requested.setChargeItemId(8L);
        requested.setRefundQty(1);
        requested.setRefundAmount(new BigDecimal("999.99"));
        RefundRequest request = new RefundRequest();
        request.setRefundOrderId(42L);
        request.setItems(List.of(requested));
        when(refundOrderMapper.selectOne(any(Wrapper.class))).thenReturn(order);
        when(invoiceMapper.selectOne(any(Wrapper.class))).thenReturn(invoice);
        when(registrationMapper.selectById(5L)).thenReturn(registration);
        when(chargeItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of(charge));
        when(refundItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of());
        when(refundItemMapper.insert(any(RefundItem.class))).thenReturn(1);
        when(refundOrderMapper.updateById(order)).thenReturn(1);
        when(invoiceMapper.updateById(invoice)).thenReturn(1);

        refundService.refund(request);

        ArgumentCaptor<RefundItem> itemCaptor = ArgumentCaptor.forClass(RefundItem.class);
        verify(refundItemMapper).insert(itemCaptor.capture());
        assertEquals(42L, itemCaptor.getValue().getRefundOrderId());
        assertEquals(8L, itemCaptor.getValue().getChargeItemId());
        assertEquals(new BigDecimal("10.00"), itemCaptor.getValue().getRefundAmount());
        assertEquals(new BigDecimal("10.00"), order.getTotalRefundAmount());
        assertEquals("已退费", order.getStatus());
        assertEquals("部分退费", invoice.getStatus());
        assertEquals("门诊发票", invoice.getInvoiceType());
        assertEquals(new BigDecimal("100.00"), invoice.getTotalAmount());
        assertEquals(new BigDecimal("60.00"), invoice.getCashAmount());
        assertEquals(new BigDecimal("20.00"), invoice.getSelfPayAmount());
        assertEquals(new BigDecimal("20.00"), invoice.getReimburseAmount());
        assertEquals(new BigDecimal("80.00"), invoice.getActualAmount());
        assertEquals(new BigDecimal("0.00"), invoice.getDiffAmount());
    }

    @Test
    void refundMarksInvoiceRefundedWhenEveryChargeQuantityHasBeenReturned() {
        Invoice invoice = invoice("已收费");
        RefundOrder order = new RefundOrder();
        order.setId(42L);
        order.setInvoiceNo("INV-1");
        order.setStatus("待退费");
        ChargeItem charge = chargeItem(8L, 1, 1, "10.00");
        RefundRecordDTO requested = new RefundRecordDTO();
        requested.setChargeItemId(8L);
        requested.setRefundQty(1);
        RefundRequest request = new RefundRequest();
        request.setRefundOrderId(42L);
        request.setItems(List.of(requested));
        when(refundOrderMapper.selectOne(any(Wrapper.class))).thenReturn(order);
        when(invoiceMapper.selectOne(any(Wrapper.class))).thenReturn(invoice);
        when(registrationMapper.selectById(5L)).thenReturn(registration());
        when(chargeItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of(charge));
        when(refundItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of());
        when(refundItemMapper.insert(any(RefundItem.class))).thenReturn(1);
        when(refundOrderMapper.updateById(order)).thenReturn(1);
        when(invoiceMapper.updateById(invoice)).thenReturn(1);

        refundService.refund(request);

        assertEquals("已退费", invoice.getStatus());
    }

    @Test
    void refundRejectsExcessQuantityBeforeWritingAnyDetails() {
        Invoice invoice = invoice("已收费");
        RefundOrder order = new RefundOrder();
        order.setId(42L);
        order.setInvoiceNo("INV-1");
        order.setStatus("待退费");
        ChargeItem charge = chargeItem(8L, 1, 1, "10.00");
        RefundRecordDTO requested = new RefundRecordDTO();
        requested.setChargeItemId(8L);
        requested.setRefundQty(2);
        RefundRequest request = new RefundRequest();
        request.setRefundOrderId(42L);
        request.setItems(List.of(requested));
        when(refundOrderMapper.selectOne(any(Wrapper.class))).thenReturn(order);
        when(invoiceMapper.selectOne(any(Wrapper.class))).thenReturn(invoice);
        when(registrationMapper.selectById(5L)).thenReturn(registration());
        when(chargeItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of(charge));
        when(refundItemMapper.selectList(any(Wrapper.class))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> refundService.refund(request));
        verify(refundItemMapper, never()).insert(any(RefundItem.class));
        verify(refundOrderMapper, never()).updateById(order);
    }

    private Invoice invoice(String status) {
        Invoice invoice = new Invoice();
        invoice.setId(4L);
        invoice.setInvoiceNo("INV-1");
        invoice.setRegistrationId(5L);
        invoice.setInvoiceType("门诊发票");
        invoice.setTotalAmount(new BigDecimal("100.00"));
        invoice.setCashAmount(new BigDecimal("60.00"));
        invoice.setSelfPayAmount(new BigDecimal("20.00"));
        invoice.setReimburseAmount(new BigDecimal("20.00"));
        invoice.setActualAmount(new BigDecimal("80.00"));
        invoice.setDiffAmount(new BigDecimal("0.00"));
        invoice.setStatus(status);
        invoice.setCreatedAt(LocalDateTime.of(2026, 11, 2, 12, 0));
        return invoice;
    }

    private Registration registration() {
        Registration registration = new Registration();
        registration.setId(5L);
        registration.setPatientId(6L);
        registration.setSettleType("医保");
        return registration;
    }

    private Patient patient() {
        Patient patient = new Patient();
        patient.setId(6L);
        patient.setCaseNo("10001");
        patient.setName("测试患者");
        return patient;
    }

    private ChargeItem chargeItem(Long id, int quantity, int times, String price) {
        ChargeItem charge = new ChargeItem();
        charge.setId(id);
        charge.setItemName("检查");
        charge.setPrice(new BigDecimal(price));
        charge.setQuantity(quantity);
        charge.setTimes(times);
        charge.setUnit("次");
        return charge;
    }
}
