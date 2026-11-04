package com.neuedu.HospitalBackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用统一返回对象
 * 后端所有接口统一使用该对象返回，确保前端能够统一解析状态码，消息以及数据字段
 * @param <T>响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult <T>{
    /**
     * HTTP 业务状态码，200 表示成功，其他值表示失败。
     */
    //响应状态码
    private int code;

    /**
     * 用户可读提示信息。
     */
    // 文档注释：响应提示文本
    private String message;

    /**
     * 返回数据主体。
     */
    // 文档注释：响应数据主体
    private T data;

    /**
     * 成功返回，默认状态码 200。
     */
    // 文档注释：构造包含数据的统一成功响应。
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    /**
     * 成功返回，可自定义消息。
     */
    // 文档注释：构造包含数据的统一成功响应。
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    /**
     * 失败返回。
     */
    // 文档注释：构造包含错误消息的统一失败响应。
    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(500, message, null);
    }
}
