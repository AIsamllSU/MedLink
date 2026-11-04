package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.BackRegisterRecordDTO;

import java.util.List;

/**
 * 退号业务服务接口
 */
public interface BackRegisterService {
    //查询退号页面中所有的挂号记录
    List<BackRegisterRecordDTO> listRecords();

    //执行挂号后退号的状态更新并返回业务结果    根据id
    BackRegisterRecordDTO backRegister(Long registrationId);
}
