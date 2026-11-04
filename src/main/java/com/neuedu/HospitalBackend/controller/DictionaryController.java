package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.ChargeCatalogRequest;
import com.neuedu.HospitalBackend.dto.DepartmentRequest;
import com.neuedu.HospitalBackend.dto.DoctorRequest;
import com.neuedu.HospitalBackend.entity.ChargeCatalogItem;
import com.neuedu.HospitalBackend.entity.Department;
import com.neuedu.HospitalBackend.entity.Doctor;
import com.neuedu.HospitalBackend.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接受前端传递 科室、医生、收费目录的查询、新增、以及更新请求的控制器
 */
@RestController
@RequestMapping("/api/dictionaries")
//解决前后端分离跨域问题
@CrossOrigin
//自动给final修饰的成员变量生成构造方法
@RequiredArgsConstructor
public class DictionaryController {
    //医院字典业务服务依赖
    private final DictionaryService dictionaryService;

    @GetMapping("/departments")
    public ApiResult<List<Department>> departments(){
        return ApiResult.success(dictionaryService.lisDepartments());
    }

    @GetMapping("/doctors")
    public ApiResult<List<Doctor>> doctors(){
        return ApiResult.success(dictionaryService.listDoctors());
    }

    @GetMapping("/charge-items")
    public ApiResult<List<ChargeCatalogItem>> chargeCatalogItem(){
        return ApiResult.success(dictionaryService.listChargeCatalog());
    }

    //新增科室
    @PostMapping("/departments")
    public ApiResult<Department> createDepartment(@RequestBody DepartmentRequest request){
        return ApiResult.success("科室已创建",dictionaryService.saveDepartment(null,request));
    }
    //更新科室
    @PutMapping("/departments/{id}")
    public ApiResult<Department> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request){
        return ApiResult.success("科室已更新",dictionaryService.saveDepartment(id,request));
    }

    //新增医生
    @PostMapping("/doctors")
    public ApiResult<Doctor> createDoctor(@RequestBody DoctorRequest request){
        return ApiResult.success("医生已创建",dictionaryService.saveDoctor(null,request));
    }
    //修改医生
    @PutMapping("/doctors/{id}")
    public ApiResult<Doctor> updateDoctor(@PathVariable Long id,@RequestBody DoctorRequest request){
        return ApiResult.success("医生已更新",dictionaryService.saveDoctor(id,request));
    }

    //新增收费项目
    @PostMapping("/charge-items")
    public ApiResult<ChargeCatalogItem> saveChargeCatalog(@RequestBody ChargeCatalogRequest request){
        return ApiResult.success("收费项目已创建",dictionaryService.saveChargeCatalog(null,request));
    }
    //修改收费项目
    @PutMapping("/charge-items/{id}")
    public ApiResult<ChargeCatalogItem> updateChargeCatalog(@PathVariable Long id,@RequestBody
                                                                        ChargeCatalogRequest request){
        return ApiResult.success("收费项目已更新",dictionaryService.saveChargeCatalog(id,request));
    }
}
