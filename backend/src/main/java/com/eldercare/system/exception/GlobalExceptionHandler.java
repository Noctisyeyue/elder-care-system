package com.eldercare.system.exception;

import com.eldercare.system.util.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一拦截 Controller 层抛出的异常并转成 ApiResult 返回
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常 → 返回对应 code 和 message
     *
     * @param e 业务异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验失败 → 汇总所有字段错误信息返回
     *
     * @param e 参数校验异常
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", message);
        return ApiResult.fail(400, message);
    }

    /**
     * 权限不足（@PreAuthorize 拦截）→ 返回 403。
     *
     * <p>注解式权限校验通过 AOP 在 Controller 方法抛出 AccessDeniedException，
     * 会优先于 Security 的 ExceptionTranslationFilter 被 @RestControllerAdvice 捕获，
     * 因此需在此显式处理，避免落到兜底的 500。
     *
     * @param e 权限异常
     * @return 统一响应结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResult<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ApiResult.fail(403, "权限不足");
    }

    /**
     * 未预料的异常 → 打日志，前端只返回通用提示
     *
     * @param e 未处理异常
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return ApiResult.fail(500, "服务器内部错误，请稍后重试");
    }
}
