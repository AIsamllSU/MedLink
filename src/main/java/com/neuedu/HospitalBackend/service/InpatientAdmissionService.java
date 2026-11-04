package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.InpatientAdmissionDTO;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionRequest;
import com.neuedu.HospitalBackend.entity.InpatientAdmission;

import java.util.List;

/**
 * 住院登记的业务层接口
 */
public interface InpatientAdmissionService {
    //1、按照住院时间倒叙查询住院登记记录
    List<InpatientAdmissionDTO> listAdmissions();

    //2、校验住院登记请求，生成住院号并保存记录
    InpatientAdmissionDTO createAdmission(InpatientAdmissionRequest request);
}
