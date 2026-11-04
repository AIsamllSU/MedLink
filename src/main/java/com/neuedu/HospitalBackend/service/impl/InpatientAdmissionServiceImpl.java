package com.neuedu.HospitalBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionDTO;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionRequest;
import com.neuedu.HospitalBackend.entity.InpatientAdmission;
import com.neuedu.HospitalBackend.mapper.InpatientAdmissionMapper;
import com.neuedu.HospitalBackend.service.InpatientAdmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 住院登记实现类
 */
@Service
@RequiredArgsConstructor
public class InpatientAdmissionServiceImpl implements InpatientAdmissionService {
    //住院登记的持久层依赖
    private final InpatientAdmissionMapper inpatientAdmissionMapper;

    //住院号生成使用的日期格式
    private static final DateTimeFormatter INPATIENT_NUMBER_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    //查询所有的登记记录
    @Override
    public List<InpatientAdmissionDTO> listAdmissions() {
        List<InpatientAdmission> admissions = inpatientAdmissionMapper.selectList(
                                new QueryWrapper<InpatientAdmission>().orderByDesc("created_at"));

        List<InpatientAdmissionDTO> result = new ArrayList<>(admissions.size());
        for(InpatientAdmission admission:admissions){
            result.add(toDto(admission));
        }
        return result;
    }

    /**
     * 效验住院登录请求、生成住院号并保存记录
     * @param request
     * @return
     */
    @Override
    public InpatientAdmissionDTO createAdmission(InpatientAdmissionRequest request) {
        //1、判断是否为空请求
        if(request==null){
            throw new IllegalArgumentException("住院登记请求不能为空");
        }
        //因为效验功能都一样，单独封装为一个效验方法
        //2、判断病历号是否为空，为空则抛出异常
        requireText(request.getCaseNo(),"病历号不能为空");
        //3、判断患者姓名是否为空，为空则抛出异常
        requireText(request.getName(),"患者姓名不能为空");
        //4、入院科室不能为空，为空则抛出异常
        requireText(request.getDepartment(),"入院科室不能为空");
        requireText(request.getWard(),"病区不能为空");
        //5、年龄校验、年龄不能小于0
        if(request.getAge()!=null&&request.getAge()<0){
            throw new IllegalArgumentException("年龄不能小于0");
        }
        //6、住院的预交押金不能小于0
        if(request.getDeposit()!=null&&request.getDeposit().signum()<0){
            throw new IllegalArgumentException("预交押金不能小于0");
        }
        //7、创建住院登记对象
        InpatientAdmission admission = new InpatientAdmission();
        //8、生成一个住院号保存  ZY是（住院首字母）+当前系统时间（按照 定义的日期格式生成）+UUID随机32位替换 有“-”的字符截取8位最后转换为大写
        //                                                      日期格式不会改，因此定义成常量
        admission.setInpatientNo("ZY"+ LocalDate.now().format(INPATIENT_NUMBER_DATE)+
                UUID.randomUUID().toString().replace("-","").substring(0,8).toUpperCase());
        //9、获取页面传递过来的参数，映射到实体中
        admission.setCaseNo(request.getCaseNo().trim());//去空格
        admission.setName(request.getName().trim());
        admission.setSex(request.getSex());//下拉选不需要
        admission.setBirthday(request.getBirthday());
        admission.setAge(request.getAge());
        admission.setIdCard(request.getIdCard());
        admission.setPhone(request.getPhone());
        admission.setAddress(request.getAddress());
        admission.setAdmissionDate(request.getAdmissionDate()==null?LocalDate.now():request.getAdmissionDate());
        admission.setAdmissionSource(defaultText(request.getAdmissionSource(),"门诊"));
        admission.setDepartment(request.getDepartment().trim());
        admission.setWard(request.getWard().trim());
        admission.setBedNo(request.getBedNo());
        admission.setDoctor(request.getDoctor());
        admission.setDiagnosis(request.getDiagnosis());
        admission.setDeposit(request.getDeposit()==null? BigDecimal.ZERO:request.getDeposit());
        admission.setStatus("待入院");
        //10、插入数据库
        inpatientAdmissionMapper.insert(admission);
        //11、转换为DTO类型返回给前端
        return toDto(admission);
    }
    //定义去除输入文本的首尾空白，输入为空时，使用指定默认值
    private String defaultText(String value, String fallback) {
        return value==null||value.isBlank()?fallback:value.trim();
    }

    //校验请求中相关的业务条件
    private void requireText(String value,String message){
        //如果参数为空，且不能是空白字符、table、换行符
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException(message);
        }
    }




    //将数据库实体转换为接口响应的DTO
    private InpatientAdmissionDTO toDto(InpatientAdmission admission){
        InpatientAdmissionDTO dto = new InpatientAdmissionDTO();
        dto.setId(admission.getId());
        dto.setInpatientNo(admission.getInpatientNo());
        dto.setCaseNo(admission.getCaseNo());
        dto.setName(admission.getName());
        dto.setSex(admission.getSex());
        dto.setBirthday(admission.getBirthday());
        dto.setAge(admission.getAge());
        dto.setIdCard(admission.getIdCard());
        dto.setPhone(admission.getPhone());
        dto.setAddress(admission.getAddress());
        dto.setAdmissionDate(admission.getAdmissionDate());
        dto.setAdmissionSource(admission.getAdmissionSource());
        dto.setDepartment(admission.getDepartment());
        dto.setWard(admission.getWard());
        dto.setBedNo(admission.getBedNo());
        dto.setDoctor(admission.getDoctor());
        dto.setDiagnosis(admission.getDiagnosis());
        dto.setDeposit(admission.getDeposit());
        dto.setStatus(admission.getStatus());
        return dto;
    }
}
