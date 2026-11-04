package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.RegistrationCreateRequest;
import com.neuedu.HospitalBackend.dto.RegistrationRecordDTO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RegistrationServiceImplTest {
    @Resource
    RegistrationService registrationService;

    @Test
    void testListRecords(){
        List<RegistrationRecordDTO> list = registrationService.listRecords();
        for(RegistrationRecordDTO dto:list){
            System.out.println("挂号ID:"+dto.getPid());
            System.out.println("患者姓名:"+dto.getName());
            System.out.println("科室名称:"+dto.getDepartment());
            System.out.println(dto);
        }
    }

    @Test
    void testRegister(){
        RegistrationCreateRequest request = new RegistrationCreateRequest();
        request.setCaseNo("CASE20261029001");
        request.setName("张三");
        request.setSex("男");
        request.setIdCard("410727198810104561");
        request.setBirthday(LocalDate.of(1988,10,10));
        request.setAge(45);
        request.setMedicalCard("YIBAO00123");
        request.setAddress("新乡市红旗区XX小区");
        request.setSettleType("医保结算");
        request.setMedicalType("职工医保");
        request.setPatientType("普通患者");
        request.setDepartment("内科");
        request.setDoctor("李医生");
        request.setDrLevel("专家号");
        request.setVisitDate(LocalDate.now());
        request.setRegPay(new BigDecimal("25"));

        RegistrationRecordDTO result = registrationService.createRegistration(request);
        System.out.println("================挂号成功===============");
        System.out.println(result);
    }

}