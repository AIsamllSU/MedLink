package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.RegistrationCreateRequest;
import com.neuedu.HospitalBackend.dto.RegistrationRecordDTO;
import com.neuedu.HospitalBackend.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/register")
@CrossOrigin
public class RegisterController {
    private final RegistrationService registrationService;

    @GetMapping("/list")
    public ApiResult<List<RegistrationRecordDTO>> list(){
        return ApiResult.success(registrationService.listRecords());
    }

    @PostMapping
    // 文档注释：创建或保存create 对应的业务数据。
    public ApiResult<RegistrationRecordDTO> create(@RequestBody RegistrationCreateRequest request) {
        return ApiResult.success("挂号成功", registrationService.createRegistration(request));
    }
}
