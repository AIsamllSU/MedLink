package com.neuedu.HospitalBackend.dto;

/**
 * 接收医生新增或修改请求，记录组件：所属科室ID，医生编码，医生姓名，医生职称，联系电话，业务状态
 */
public record DoctorRequest(Long departmentId,String doctorCode,String name,String title,String phone,String status) {
}
