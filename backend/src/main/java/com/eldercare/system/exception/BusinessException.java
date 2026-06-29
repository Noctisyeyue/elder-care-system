package com.eldercare.system.exception;

/**
 * 业务异常，在 Service 层抛出后由 GlobalExceptionHandler 统一处理
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    /**
     * @param code    业务状态码
     * @param message 异常描述
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 默认 code=500 的业务异常
     *
     * @param message 异常描述
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public Integer getCode() { return code; }
}
