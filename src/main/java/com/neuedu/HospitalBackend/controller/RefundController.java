package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import com.neuedu.HospitalBackend.dto.RefundRecordDTO;
import com.neuedu.HospitalBackend.dto.RefundPreparationDTO;
import com.neuedu.HospitalBackend.dto.RefundRequest;
import com.neuedu.HospitalBackend.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refund")
@CrossOrigin
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    @GetMapping("/prepare")
    public ApiResult<RefundPreparationDTO> prepare(@RequestParam String invoiceNo) {
        return ApiResult.success("退费项目查询成功", refundService.prepareRefund(invoiceNo));
    }

    /**
     * 查询退费业务
     * @return
     */
    @GetMapping("/list")
    public ApiResult<?> list(@RequestParam(required = false) String invoiceNo) {
        if (invoiceNo != null && !invoiceNo.isBlank()) {
            return ApiResult.success("退费项目查询成功", refundService.prepareRefund(invoiceNo));
        }
        return ApiResult.success(refundService.listRefundRecords());
    }

    @PostMapping
    public ApiResult<List<RefundRecordDTO>> refund(@RequestBody RefundRequest request){
        return ApiResult.success("退费处理已提交",refundService.refund(request));
    }
}
