package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionDTO;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionRequest;
import com.neuedu.HospitalBackend.service.InpatientAdmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inpatient-register")
@CrossOrigin
public class InpatientAdmissionController {
    //业务依赖
    private final InpatientAdmissionService inpatientAdmissionService;
    /**
     * 查询住院记录
     */
    @GetMapping("/list")
    public ApiResult<List<InpatientAdmissionDTO>> list(){
        return ApiResult.success(inpatientAdmissionService.listAdmissions());
    }

    /**
     * 创建入院登记
     * @param request
     * @return
     */
    @PostMapping
    public ApiResult<InpatientAdmissionDTO> create(@RequestBody InpatientAdmissionRequest request) {
        return ApiResult.success("住院登记成功", inpatientAdmissionService.createAdmission(request));
    }
}
