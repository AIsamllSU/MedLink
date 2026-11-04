package com.neuedu.HospitalBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("account_profile")
public class AccountProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    //用户名
    private String displayName;
    //岗位名称
    private String jobTitle;
    //科室名称
    private String department;
    //手机
    private String phone;
    private String email;
    
    //是否启用邮件通知
    private Boolean emailNotifications;
}
