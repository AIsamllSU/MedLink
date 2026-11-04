package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.ChargeCreateRequest;
import com.neuedu.HospitalBackend.dto.ChargeItemDTO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChargeItemServiceTest {
    @Resource
    ChargeService chargeService;


    @Test
    void listChargeItems() {
        List<ChargeItemDTO> items = chargeService.listChargeItems();

        items.forEach(item ->{
            System.out.println("项目名称："+item.getName());
            System.out.println("科室名称："+item.getDepartment());
            System.out.println("单价："+item.getPrice()+",金额："+item.getAmount());
        });
    }

    @Test
    void saveCharge(){
        ChargeCreateRequest request = new ChargeCreateRequest();
        List<ChargeItemDTO> list = new ArrayList<>();

        ChargeItemDTO dto1 = new ChargeItemDTO();
        dto1.setName("血常规");
        dto1.setSpec("常规");
        dto1.setPrice(BigDecimal.valueOf(15.0));
        dto1.setQuantity(1);
        dto1.setUnit("次");
        dto1.setTimes(1);
        dto1.setAmount(BigDecimal.valueOf(15.0));
        dto1.setDepartment("测试科室");
        dto1.setSelected(true);
        list.add(dto1);

        request.setItems(list);

        List<ChargeItemDTO> result = chargeService.saveChargeItems(request);

        result.forEach(item->{
            System.out.println(item.getName()+"|"+item.getDepartment());
        });
    }
}