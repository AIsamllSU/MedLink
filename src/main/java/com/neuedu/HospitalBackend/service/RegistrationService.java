package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.RegistrationCreateRequest;
import com.neuedu.HospitalBackend.dto.RegistrationRecordDTO;
import com.neuedu.HospitalBackend.entity.Registration;

import java.util.List;

/**
 * 挂号记录服务接口
 */
public interface RegistrationService {
    //查询业务记录
    List<RegistrationRecordDTO> listRecords();


    //创建患者档案并保存门诊挂号记录。
    RegistrationRecordDTO createRegistration(RegistrationCreateRequest request);
 }
