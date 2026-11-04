package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.ChargeCreateRequest;
import com.neuedu.HospitalBackend.dto.ChargeItemDTO;
import com.neuedu.HospitalBackend.entity.ChargeItem;
import com.neuedu.HospitalBackend.mapper.ChargeItemMapper;
import com.neuedu.HospitalBackend.mapper.DepartmentMapper;
import com.neuedu.HospitalBackend.service.impl.ChargeItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
// 2026-11-02 新增：验证收费明细按 ID 更新以及新项目插入。
class ChargeItemServiceImplTest {
    @Mock
    private ChargeItemMapper chargeItemMapper;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private ChargeItemServiceImpl chargeItemService;

    @Test
    void saveExistingChargeItemUpdatesInsteadOfInserting() {
        when(chargeItemMapper.updateById(any(ChargeItem.class))).thenReturn(1);

        List<ChargeItemDTO> result = chargeItemService.saveChargeItems(request(item(42L)));

        ArgumentCaptor<ChargeItem> captor = ArgumentCaptor.forClass(ChargeItem.class);
        verify(chargeItemMapper).updateById(captor.capture());
        verify(chargeItemMapper, never()).insert(any(ChargeItem.class));
        assertEquals(42L, captor.getValue().getId());
        assertEquals(new BigDecimal("30.00"), captor.getValue().getAmount());
        assertEquals(42L, result.get(0).getId());
    }

    @Test
    void saveNewChargeItemInsertsAndReturnsGeneratedId() {
        doAnswer(invocation -> {
            ((ChargeItem) invocation.getArgument(0)).setId(99L);
            return 1;
        }).when(chargeItemMapper).insert(any(ChargeItem.class));

        List<ChargeItemDTO> result = chargeItemService.saveChargeItems(request(item(null)));

        ArgumentCaptor<ChargeItem> captor = ArgumentCaptor.forClass(ChargeItem.class);
        verify(chargeItemMapper).insert(captor.capture());
        verify(chargeItemMapper, never()).updateById(any(ChargeItem.class));
        assertEquals(99L, result.get(0).getId());
        assertEquals(new BigDecimal("30.00"), captor.getValue().getAmount());
    }

    private ChargeCreateRequest request(ChargeItemDTO item) {
        ChargeCreateRequest request = new ChargeCreateRequest();
        request.setItems(List.of(item));
        return request;
    }

    private ChargeItemDTO item(Long id) {
        ChargeItemDTO item = new ChargeItemDTO();
        item.setId(id);
        item.setName("血常规");
        item.setPrice(new BigDecimal("10.00"));
        item.setQuantity(3);
        item.setTimes(1);
        return item;
    }
}
