package com.neuedu.HospitalBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.BackRegisterRecordDTO;
import com.neuedu.HospitalBackend.dto.RegistrationRecordDTO;
import com.neuedu.HospitalBackend.entity.Department;
import com.neuedu.HospitalBackend.entity.Patient;
import com.neuedu.HospitalBackend.entity.Registration;
import com.neuedu.HospitalBackend.mapper.PatientMapper;
import com.neuedu.HospitalBackend.mapper.RegistrationMapper;
import com.neuedu.HospitalBackend.service.BackRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 退号业务服务实现类
 * 查询挂号记录并更新状态为已退号
 */
@Service
@RequiredArgsConstructor
public class BackRegisterServiceImpl implements BackRegisterService {
    //挂号的持久层接口
    private final RegistrationMapper registrationMapper;
    //患者接口依赖
    private final PatientMapper patientMapper;


    //查询挂号记录并转换为页面所需的列表DTO
    @Override
    public List<BackRegisterRecordDTO> listRecords() {
        //查询所有的挂号记录
        List<Registration> registrations = registrationMapper.selectList(new QueryWrapper<Registration>().orderByDesc("created_at"));

        List<BackRegisterRecordDTO> result = new ArrayList<>();
        //遍历所有挂号记录，拿到患者主键ID
        for(Registration reg:registrations){
            //根据id获取患者信息
            Patient patient = patientMapper.selectById(reg.getPatientId());
            //装进集合，集合中包含的就是：挂号部分信息+患者部分信息
            result.add(toRecord(reg,patient));
        }
        return result;//返回给前端
    }

    /**
     * 执行退号更新状态后返回给前端
     * @param registrationId
     * @return
     */
    @Override
    public BackRegisterRecordDTO backRegister(Long registrationId) {
        //1、通过挂号ID查询返回挂号实体对象
        Registration registration = registrationMapper.selectById(registrationId);

        //2、判断挂号记录是否为null，为空则抛出异常
        if(registration==null){
            throw new IllegalArgumentException("未找对对应的挂号记录");
        }
        //3、不为空，表示已经挂号，将挂号状态修改为 【已退号】
        registration.setStatus("已退号");
        registrationMapper.updateById(registration);
        //4、根据toRecord方法，将退号的状态（挂号记录中），患者信息合并返回给前端
        Patient patient = patientMapper.selectById(registration.getPatientId());
        return toRecord(registration,patient);
    }


    /**
     * 将挂号实体，患者组合为挂号列表DTO
     * @param reg
     * @param patient
     * @return
     */
    private BackRegisterRecordDTO toRecord(Registration reg, Patient patient){
        BackRegisterRecordDTO dto = new BackRegisterRecordDTO();
        dto.setId(reg.getId());
        //病历号
        dto.setCaseNo(patient == null ? reg.getInvoiceNo() : patient.getCaseNo());
        //姓名
        dto.setName(patient == null ? "未知" : patient.getName());
        dto.setSex(patient==null?"男":patient.getGender());
        dto.setBirthday(patient == null ? null : patient.getBirthday());
        dto.setAge(18);
        dto.setPid(patient == null ? reg.getInvoiceNo() : patient.getIdCard());
        dto.setRegInvoice(reg.getInvoiceNo());
        dto.setSettleType(reg.getSettleType());
        dto.setRegLevel(reg.getRegLevel());
        dto.setRegDate(reg.getRegDate());
        dto.setVisitDate(reg.getVisitDate());
        dto.setDiagnosed(reg.getDiagnoseStatus());
        dto.setStatus(reg.getStatus());
        dto.setRealPay(reg.getAmount());
        dto.setDepartment("内科门诊");
        return dto;
    }
}
