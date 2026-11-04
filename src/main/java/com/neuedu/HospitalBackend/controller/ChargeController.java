package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.ChargeCreateRequest;
import com.neuedu.HospitalBackend.dto.ChargeItemDTO;
import com.neuedu.HospitalBackend.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收费控制器
 * 负责返回项目项目列表以及保存新增收费项
 */
@RestController
@RequestMapping("/api/charge")
@CrossOrigin
@RequiredArgsConstructor
public class ChargeController {
    //收费业务依赖
    private final ChargeService chargeService;

    //查询所有门诊收费列表
    @GetMapping("/items")
    public ApiResult<List<ChargeItemDTO>> listItem(){
        return ApiResult.success(chargeService.listChargeItems());
    }
    //保存本次收费门诊项目，并返回保存后的收费列表
    @PostMapping("/save")
    public ApiResult<List<ChargeItemDTO>> save(@RequestBody ChargeCreateRequest request){
        return ApiResult.success("收费项目已保存",chargeService.saveChargeItems(request));
    }
}
