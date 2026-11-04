package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.AccountProfileDTO;
import com.neuedu.HospitalBackend.dto.AccountProfileRequest;
import com.neuedu.HospitalBackend.service.AccountProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/profile")
@CrossOrigin
@RequiredArgsConstructor
public class AccountProfileController {
    // 文档注释：演示账户资料业务服务依赖
    private final AccountProfileService accountProfileService;

    @GetMapping
    // 文档注释：读取演示账户资料并转换为响应 DTO。
    public ApiResult<AccountProfileDTO> getProfile() {
        return ApiResult.success(accountProfileService.getProfile());
    }

    @PutMapping
    // 文档注释：校验并更新演示账户资料。
    public ApiResult<AccountProfileDTO> updateProfile(@RequestBody AccountProfileRequest request) {
        return ApiResult.success("个人资料已保存", accountProfileService.updateProfile(request));
    }
}
