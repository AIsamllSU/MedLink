package com.neuedu.HospitalBackend.dto;

/**
 * 接收新增科室或跟新请求。记录组件：科室编码，科室名称，科室简介，业务状态
 */
public record DepartmentRequest(String deptCode,String deptName,String description,String status) {
}
