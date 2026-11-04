package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.BackRegisterRecordDTO;
import com.neuedu.HospitalBackend.service.BackRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/back-register")
@RequiredArgsConstructor
public class BackRegisterController {
    //业务对象
    private final BackRegisterService backRegisterService;
    //退号页面数据
    @GetMapping("/list")
    public ApiResult<List<BackRegisterRecordDTO>> list(){
        return ApiResult.success(backRegisterService.listRecords());
    }

    //退号
    @PostMapping("/{id}")
    public ApiResult<BackRegisterRecordDTO> backRegister(@PathVariable Long id){
        return ApiResult.success("退号成功",backRegisterService.backRegister(id));
    }
}

