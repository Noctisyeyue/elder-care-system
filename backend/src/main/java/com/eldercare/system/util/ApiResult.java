package com.eldercare.system.util;

/**
 * 统一接口响应结果
 *
 * @param <T> 响应数据的类型
 */
public class ApiResult<T> {

    /** 业务状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /**
     * 创建空响应结果
     */
    public ApiResult() {}

    /**
     * 创建指定状态、消息和数据的响应结果
     *
     * @param code    业务状态码
     * @param message 响应消息
     * @param data    响应数据
     */
    private ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

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

    /**
     * 获取业务状态码
     *
     * @return 业务状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置业务状态码
     *
     * @param code 业务状态码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     *
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }
}
