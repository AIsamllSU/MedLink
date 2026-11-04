package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.InpatientAdmissionDTO;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionRequest;
import com.neuedu.HospitalBackend.entity.InpatientAdmission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class InpatientAdmissionServiceTest {
    @Autowired
    InpatientAdmissionService inpatientAdmissionService;

    @Test
    void listAdmissions() {
        List<InpatientAdmissionDTO> dtos = inpatientAdmissionService.listAdmissions();
        for(InpatientAdmissionDTO admission:dtos){
            System.out.println(admission);
        }
    }

    @Test
    void testCreateAdmission(){
        InpatientAdmissionRequest request = new InpatientAdmissionRequest();
        request.setCaseNo("1008611");//验证校验病历号
        request.setName("张三");//校验患者姓名
        request.setDepartment("外科");//科室信息不能为空
        request.setWard("外科病区");//病区信息
        InpatientAdmissionDTO dto = inpatientAdmissionService.createAdmission(request);
        assertNotNull(dto);
        assertNotNull(dto.getInpatientNo());
        assertEquals("待入院",dto.getStatus());
        System.out.println("新增成功，住院号："+dto.getInpatientNo());//ZY202611030E7DF68E

    }
}