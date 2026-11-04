package com.neuedu.HospitalBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.RegistrationCreateRequest;
import com.neuedu.HospitalBackend.dto.RegistrationRecordDTO;
import com.neuedu.HospitalBackend.entity.Department;
import com.neuedu.HospitalBackend.entity.Doctor;
import com.neuedu.HospitalBackend.entity.Patient;
import com.neuedu.HospitalBackend.entity.Registration;
import com.neuedu.HospitalBackend.mapper.DepartmentMapper;
import com.neuedu.HospitalBackend.mapper.DoctorMapper;
import com.neuedu.HospitalBackend.mapper.PatientMapper;
import com.neuedu.HospitalBackend.mapper.RegistrationMapper;
import com.neuedu.HospitalBackend.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 挂号业务实现类
 *
 * 主要功能：生成挂号单，插入患者信息、关联科室与医生，并封装返回给页面的数据
 */
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    //挂号记录
    private final RegistrationMapper registrationMapper;
    //患者mapper依赖
    private final PatientMapper patientMapper;
    //科室mapper依赖
    private final DepartmentMapper departmentMapper;
    //医生mapper依赖
    private final DoctorMapper doctorMapper;

    @Override
    public List<RegistrationRecordDTO> listRecords() {
        //查询挂号记录
        List<Registration> registrations = registrationMapper.selectList(new QueryWrapper<Registration>().orderByDesc("created_at"));

        List<RegistrationRecordDTO> result = new ArrayList<>();
        //遍历挂号记录
        for(Registration reg:registrations){
            //根据id获取患者信息
            Patient patient = patientMapper.selectById(reg.getPatientId());
            //根据id获取科室信息
            Department department = departmentMapper.selectById(reg.getDepartmentId());
            //封装结果
            result.add(toRecord(reg,patient,department));
        }
        return result;
    }
    @Override
    // 文档注释：创建患者档案并保存门诊挂号记录。
    public RegistrationRecordDTO createRegistration(RegistrationCreateRequest request) {
        // 根据业务条件执行校验或选择处理分支。
        if (request == null) {
            throw new IllegalArgumentException("挂号请求不能为空");
        }
        //生成病历号：如果前端没有传入病历号自动使用CASE+时间戳生成，否则使用传入的值
        String caseNo = request.getCaseNo() == null || request.getCaseNo().isBlank()
                ? "CASE" + System.currentTimeMillis()
                : request.getCaseNo();
        //创建患者实体类，封装患者信息
        Patient patient = new Patient();
        patient.setCaseNo(caseNo);
        patient.setName(request.getName());
        patient.setGender(request.getSex());
        patient.setIdCard(request.getIdCard());
        //设置出生日期，前端不传则默认当前日期
        patient.setBirthday(request.getBirthday() == null ? LocalDate.now() : request.getBirthday());
        //设置年龄，前端不传参则默认为0
        patient.setAge(request.getAge() == null ? 0 : request.getAge());
        patient.setMedicalCard(request.getMedicalCard());
        patient.setAddress(request.getAddress());
        patient.setSettleType(request.getSettleType());
        patient.setMedicalType(request.getMedicalType());
        patient.setPatientType(request.getPatientType());
        patientMapper.insert(patient);

        //根据前端传入的科室名称，查询科室信息
        Department department = departmentMapper.selectOne(
                new QueryWrapper<Department>().eq("dept_name", request.getDepartment())
        );
        //根据前端传入的医生姓名，查询医生信息
        Doctor doctor = doctorMapper.selectOne(
                new QueryWrapper<Doctor>().eq("name", request.getDoctor())
        );
        //新建挂号实体，封装挂号记录
        Registration registration = new Registration();
        //绑定新增患者的主键ID
        registration.setPatientId(patient.getId());
        //生成票据号 G+当前时间
        registration.setInvoiceNo("G" + System.currentTimeMillis());
        //设置科室id:没查到科室，默认1号科室
        registration.setDepartmentId(department == null ? 1L : department.getId());
        //设置医生id:没查到，则为null
        registration.setDoctorId(doctor == null ? null : doctor.getId());
        registration.setRegLevel(request.getDrLevel());
        registration.setRegDate(LocalDate.now());
        //就诊日期：前端不传，则默认今天
        registration.setVisitDate(request.getVisitDate() == null ? LocalDate.now() : request.getVisitDate());
        registration.setSource("窗口挂号");
        //默认未就诊
        registration.setDiagnoseStatus("否");
        registration.setStatus("已挂号");
        //挂号费用，不设置则默认15元
        registration.setAmount(request.getRegPay() == null ? BigDecimal.valueOf(15) : request.getRegPay());
        //结算类型
        registration.setSettleType(request.getSettleType());
        registrationMapper.insert(registration);

        return toRecord(registration, patient, department);
    }




    /**
     * 将挂号实体，患者和科室信息组合为挂号列表DTO
     * @param reg
     * @param patient
     * @param department
     * @return
     */
    private RegistrationRecordDTO toRecord(Registration reg,Patient patient,Department department){
        //前端页面性别展示
        String sex = patient ==null?"男":patient.getGender();
        //科室
        String departmentName = department==null?"内科门诊":department.getDeptName();

        RegistrationRecordDTO dto = new RegistrationRecordDTO();
        //病历号
        dto.setCaseNo(patient == null ? reg.getInvoiceNo() : patient.getCaseNo());
        //姓名
        dto.setName(patient == null ? "未知" : patient.getName());
        dto.setSex(sex);
        dto.setBirthday(patient == null ? LocalDate.now() : patient.getBirthday());
        dto.setPid(patient == null ? reg.getInvoiceNo() : patient.getIdCard());
        dto.setRegInvoice(reg.getInvoiceNo());
        dto.setSettleType(reg.getSettleType());
        dto.setRegLevel(reg.getRegLevel());
        dto.setRegDate(reg.getRegDate());
        dto.setVisitDate(reg.getVisitDate());
        dto.setDiagnosed(reg.getDiagnoseStatus());
        dto.setStatus(reg.getStatus());
        dto.setRealPay(reg.getAmount());
        dto.setDepartment(departmentName);
        return dto;
    }
}
