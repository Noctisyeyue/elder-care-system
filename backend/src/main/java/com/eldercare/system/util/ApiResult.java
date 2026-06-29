package com.eldercare.system.util;

/**
 * 统一响应封装，所有接口返回给前端的数据都用这个格式
 *
 * @param <T> 响应数据的类型
 */
public class ApiResult<T> {

    private Integer code;
    private String message;
    private T data;

    private ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @return RestBean(code=200, message="操作成功", data=data)
     */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     *
     * @return ApiResult(code=200, message="操作成功", data=null)
     */
    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(200, "操作成功", null);
    }

    /**
     * 指定失败状态码和消息
     *
     * @param code    状态码
     * @param message 失败原因
     * @return ApiResult(code=code, data=null, message=message)
     */
    public static <T> ApiResult<T> fail(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }

    /**
     * 通用失败（code=500）
     *
     * @param message 失败原因
     * @return ApiResult(code=500, data=null, message=message)
     */
    public static <T> ApiResult<T> fail(String message) {
        return new ApiResult<>(500, message, null);
    }

    // ===== getter / setter =====

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
