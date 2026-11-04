package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.AccountProfileDTO;
import com.neuedu.HospitalBackend.dto.AccountProfileRequest;
import com.neuedu.HospitalBackend.entity.AccountProfile;
import com.neuedu.HospitalBackend.mapper.AccountProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountProfileService {
    private final AccountProfileMapper accountProfileMapper;
    // 文档注释：固定演示账户的数据库主键
    private static final long DEMO_ACCOUNT_ID = 1L;

    //1、获取账户资料
    // 文档注释：读取演示账户资料并转换为响应 DTO。
    public AccountProfileDTO getProfile() {
        return toDto(requireProfile());
    }

    //2、效验更新账户资料
    public AccountProfileDTO updateProfile(AccountProfileRequest request) {
        // 文档注释：根据业务条件执行校验或选择处理分支。
        if (request == null || request.displayName() == null || request.displayName().isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        AccountProfile profile = requireProfile();
        profile.setDisplayName(request.displayName().trim());
        profile.setJobTitle(request.jobTitle());
        profile.setDepartment(request.department());
        profile.setPhone(request.phone());
        profile.setEmail(request.email());
        profile.setEmailNotifications(Boolean.TRUE.equals(request.emailNotifications()));
        accountProfileMapper.updateById(profile);
        return toDto(profile);
    }




    // 文档注释：读取固定演示账户资料，资料缺失时抛出异常。
    private AccountProfile requireProfile() {
        AccountProfile profile = accountProfileMapper.selectById(DEMO_ACCOUNT_ID);
        // 文档注释：根据业务条件执行校验或选择处理分支。
        if (profile == null) throw new IllegalStateException("演示账户资料尚未初始化");
        return profile;
    }

    // 文档注释：将数据库实体转换为接口响应 DTO。
    private AccountProfileDTO toDto(AccountProfile profile) {
        return new AccountProfileDTO(profile.getId(), profile.getDisplayName(), profile.getJobTitle(),
                profile.getDepartment(), profile.getPhone(), profile.getEmail(), profile.getEmailNotifications());
    }
}
