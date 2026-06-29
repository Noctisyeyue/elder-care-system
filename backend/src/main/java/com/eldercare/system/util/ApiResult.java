package com.eldercare.system.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口响应结果
 *
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResult<T> {

    /** 业务状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /**
     * 创建成功响应结果
     *
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    /**
     * 创建无数据的成功响应结果
     *
     * @return 成功响应结果
     */
    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(200, "操作成功", null);
    }

    /**
     * 创建指定状态码和消息的失败响应结果
     *
     * @param code    业务状态码
     * @param message 响应消息
     * @return 失败响应结果
     */
    public static <T> ApiResult<T> fail(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }

    /**
     * 创建通用失败响应结果
     *
     * @param message 响应消息
     * @return 失败响应结果
     */
    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(500, message, null);
    }
}
