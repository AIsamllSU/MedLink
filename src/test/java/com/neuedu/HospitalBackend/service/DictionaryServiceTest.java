package com.neuedu.HospitalBackend.service;

import com.neuedu.HospitalBackend.dto.DepartmentRequest;
import com.neuedu.HospitalBackend.entity.ChargeCatalogItem;
import com.neuedu.HospitalBackend.entity.Department;
import com.neuedu.HospitalBackend.entity.Doctor;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DictionaryServiceTest {
    @Autowired
    private DictionaryService service;

    @Test
    void lisDepartments() {
        List<Department>  list =service.lisDepartments();
        System.out.println("============查询科室==========");
        list.forEach(System.out::println);
    }
    @Test
    void listDoctors(){
        List<Doctor>  list =service.listDoctors();
        System.out.println("============查询医生==========");
        list.forEach(System.out::println);
    }
    @Test
    void testListChargeCatalog(){
        List<ChargeCatalogItem> chargeCatalogItems = service.listChargeCatalog();
        System.out.println("============查询收费目录，携带科室名称==========");
        for(ChargeCatalogItem item:chargeCatalogItems){
            System.out.println("id:"+item.getId()+"项目名称："+item.getItemName()
                                +"科室id："+item.getDepartmentId()+"科室名称："+item.getDepartment());
        }
    }

    @Test
    void testSaveDepartment(){
        DepartmentRequest request = new DepartmentRequest("QS1001","儿科","儿童科室","ACTIVE");
        //id为null，表示新增
        Department department = service.saveDepartment(null, request);
        System.out.println("新增成功，科室信息："+department);
    }

    @Test
    void testUpdateDepartment(){
        DepartmentRequest request = new DepartmentRequest("QS101","儿科","儿童保健科室","ACTIVE");
        //id为null，表示新增
        Department department = service.saveDepartment(6L,request);
        System.out.println("修改成功，科室信息："+department);
    }

    @Test
    void testUpdateDepartmentValidateFail(){
        DepartmentRequest request = new DepartmentRequest("","","","ACTIVE");
        //id为null，表示新增
        try {
            Department department = service.saveDepartment(null,request);
        }catch (Exception e) {
            System.out.println("捕获到了异常：" + e.getMessage());
        }
    }

    @Test
    void testUpdateDepartmentValidateFail2(){
        DepartmentRequest request = new DepartmentRequest("WK004","外科","外科","fffff");
        //id为null，表示新增
        try {
            Department department = service.saveDepartment(null,request);
        }catch (Exception e) {
            System.out.println("捕获到了异常：" + e.getMessage());
        }
    }
}