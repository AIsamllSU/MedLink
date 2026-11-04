package com.neuedu.HospitalBackend.controller;

import com.neuedu.HospitalBackend.common.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @RestControllerAdvice: 全局捕获所有`@RestController`抛出的异常，统一处理，统一返回JSON
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    /**
     * @ExceptionHandler(IllegalArgumentException.class)
     * 专门捕获非法参数异常
     * @param exception
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    //ResponseEntity：可以同时控制HTTP状态码+返回体
    //HttpStatus.BAD_REQUEST:业务状态码对应400 【代表客户端参数错误】
    public ResponseEntity<ApiResult<Void>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                //返回统一的Json格式
                .body(ApiResult.fail(exception.getMessage()));
    }
}
