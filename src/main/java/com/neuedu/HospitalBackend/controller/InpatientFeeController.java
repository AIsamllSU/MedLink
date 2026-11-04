package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.InpatientAdmissionRequest;
import com.neuedu.HospitalBackend.dto.InpatientFeeLedgerDTO;
import com.neuedu.HospitalBackend.dto.InpatientFeeRequest;
import com.neuedu.HospitalBackend.entity.InpatientAdmission;
import com.neuedu.HospitalBackend.service.InpatientFeeTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inpatient-fees")
@RequiredArgsConstructor
@CrossOrigin
public class InpatientFeeController {
    //业务依赖
    private final InpatientFeeTransactionService transactionService;

    @GetMapping("/admissions")
    public ApiResult<List<InpatientAdmission>> admissions(){
        return ApiResult.success(transactionService.listAdmissions());
    }

    /**
     * 根据id查询流水台账
     * @param admissionId
     * @return
     *
     * RestFul接口规范，把资源id放到请求路径中
     */
    @GetMapping("/{admissionId}/ledger")
    public ApiResult<InpatientFeeLedgerDTO> ledger(@PathVariable Long admissionId){
        return ApiResult.success(transactionService.ledger(admissionId));
    }

    /**
     * 根据住院id校验住院账户余额，保存流水并返回最新台账
     */
    @PostMapping("/{admissionId}/transactions")
    public ApiResult<InpatientFeeLedgerDTO> createTransaction(@PathVariable Long admissionId, @RequestBody InpatientFeeRequest request){
        return ApiResult.success("住院费用记录已保存",transactionService.createTransaction(admissionId,request));
    }
}
