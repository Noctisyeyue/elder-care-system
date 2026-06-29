package com.eldercare.system.exception;

import com.eldercare.system.util.ApiResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一拦截 Controller 层抛出的异常并转成 ApiResult 返回
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常 → 返回对应 code 和 message
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验失败 → 汇总所有字段错误信息返回
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return ApiResult.fail(400, message);
    }

    /**
     * 未预料的异常 → 兜底，返回 500
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        return ApiResult.fail(500, "服务器内部错误: " + e.getMessage());
    }
}
