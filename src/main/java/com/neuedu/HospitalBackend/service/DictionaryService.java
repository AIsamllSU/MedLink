package com.neuedu.HospitalBackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.HospitalBackend.dto.ChargeCatalogRequest;
import com.neuedu.HospitalBackend.dto.DepartmentRequest;
import com.neuedu.HospitalBackend.dto.DoctorRequest;
import com.neuedu.HospitalBackend.entity.ChargeCatalogItem;
import com.neuedu.HospitalBackend.entity.Department;
import com.neuedu.HospitalBackend.entity.Doctor;
import com.neuedu.HospitalBackend.mapper.ChargeCatalogItemMapper;
import com.neuedu.HospitalBackend.mapper.DepartmentMapper;
import com.neuedu.HospitalBackend.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 维护科室、医生、收费项目目录，执行输入校验和持久化
 */
@Service
@RequiredArgsConstructor
public class DictionaryService {
    //科室mapper依赖
    private final DepartmentMapper departmentMapper;
    //医生mapper依赖
    private final DoctorMapper doctorMapper;
    //收费目录mapper依赖
    private final ChargeCatalogItemMapper chargeCatalogItemMapper;

    //业务功能1：按照id查找科室字典列表
    public List<Department> lisDepartments(){
        return departmentMapper.selectList(new QueryWrapper<Department>().orderByAsc("id"));
    }
    //按照id查询医生字典列表
    public List<Doctor> listDoctors(){
        return doctorMapper.selectList(new QueryWrapper<Doctor>().orderByAsc("id"));
    }

    //查询收费项目目录，并补充执行科室名称
    public List<ChargeCatalogItem> listChargeCatalog(){
        List<ChargeCatalogItem> items = chargeCatalogItemMapper.selectList(new QueryWrapper<ChargeCatalogItem>().orderByAsc("id"));
        //遍历集合处理当前业务数据
        for(ChargeCatalogItem item:items){
            //获取科室ID
            Department department = item.getDepartmentId()==null?null:departmentMapper.selectById(item.getDepartmentId());
            item.setDepartment(department==null?"":department.getDeptName());
        }
        return items;
    }

    //新增或修改科室信息
    public Department saveDepartment(Long id, DepartmentRequest request){
        //1、效验业务功能，不满足时抛出异常，当前业务规则：科室编码和名称不能为空
        require(request!=null&&hasText(request.deptCode())&&hasText(request.deptName()),"科室编码和名称不能为空");
        //2、确定id不为空创建科室对象并且判断科室不存在 之后，添加新科室信息
        Department department = id==null?new Department():requireFound(departmentMapper.selectById(id),"科室不存在");
        //3、添加的信息是DTO中参数
        department.setDeptCode(request.deptCode().trim());
        department.setDeptName(request.deptName().trim());
        department.setDescription(request.description());
        department.setStatus(status(request.status()));
        //4、根据业务条件判断是新增还是修改
        if(id==null){
            departmentMapper.insert(department);
        }else{
            departmentMapper.updateById(department);
        }
        return department;
    }

    //新增或修改医生信息业务
    public Doctor saveDoctor(Long id, DoctorRequest request){
        //1、效验：请求不为空且所属科室不为空且医生编号和姓名都不为空
        require(request!=null&&request.departmentId()!=null&&hasText(request.doctorCode())
                            &&hasText(request.name()),"医生编码、姓名和所属科室不能为空");
        //2、判断科室信息
        require(departmentMapper.selectById(request.departmentId())!=null,"所属科室不存在");
        //3、判断医生信息是否存在
        Doctor doctor = id==null?new Doctor():requireFound(doctorMapper.selectById(id),"医生不存在");
        //4、设置医生信息
        doctor.setDepartmentId(request.departmentId());
        doctor.setDoctorCode(request.doctorCode().trim());
        doctor.setName(request.name().trim());//去掉空格
        doctor.setTitle(request.title());
        doctor.setPhone(request.phone());
        doctor.setStatus(request.status());
        //5、根据业务条件判断是新增还是修改
        if (id==null){
            doctorMapper.insert(doctor);
        }else{
            doctorMapper.updateById(doctor);
        }
        return doctor;
    }


    //新增或修改收费项目业务
    public ChargeCatalogItem saveChargeCatalog(Long id, ChargeCatalogRequest request){
        //1、效验(收费项目名称不能为空，价格不能小于0)
        require(request!=null&&hasText(request.itemName())&&request.price()!=null
                            &&request.price().signum()>=0,"收费项目名称不能为空，价格不能小0");
        //2、效验(收费项目要依据科室，判断科室是否存在)
        require(request.departmentId()==null||departmentMapper.selectById(request.departmentId())!=null,"执行科室不存在");
        //3、判断收费项目是否存在
        ChargeCatalogItem item = id==null?new ChargeCatalogItem():requireFound(chargeCatalogItemMapper.selectById(id),"收费项目不存在");
        //3、不存在则设置收费项目信息
        item.setItemName(request.itemName().trim());
        item.setSpec(request.spec());
        item.setPrice(request.price());
        item.setUnit(request.unit());
        item.setDepartmentId(request.departmentId());
        item.setStatus(request.status());
        //4、存在修改收费项目信息
        if(id==null){
            chargeCatalogItemMapper.insert(item);
        }else{
            chargeCatalogItemMapper.updateById(item);
        }
        return item;
    }


    // 文档注释：校验并规范化状态值，只允许业务支持的状态。
    private String status(String value) {
        // 文档注释：根据业务条件执行校验或选择处理分支。
        if (value == null || value.isBlank() || "ACTIVE".equalsIgnoreCase(value)) return "ACTIVE";
        // 文档注释：根据业务条件执行校验或选择处理分支。
        if ("INACTIVE".equalsIgnoreCase(value)) return "INACTIVE";
        throw new IllegalArgumentException("状态只能是 ACTIVE 或 INACTIVE");
    }

    // 文档注释：判断字符串是否非空且包含非空白字符。
    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    // 文档注释：校验业务条件，不满足时抛出参数异常。
    private void require(boolean condition, String message) {
        // 文档注释：根据业务条件执行校验或选择处理分支。
        if (!condition) throw new IllegalArgumentException(message);
    }

    // 文档注释：确认数据查询结果存在，不存在时抛出业务异常。
    private <T> T requireFound(T value, String message) {
        // 文档注释：根据业务条件执行校验或选择处理分支。
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }
}
