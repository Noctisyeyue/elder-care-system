package com.eldercare.system.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eldercare.system.annotation.OperationLog;
import com.eldercare.system.security.LoginUser;
import com.eldercare.system.service.OperationLogService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.IpUtil;
import com.eldercare.system.util.SensitiveDataUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Parameter;
import java.time.LocalDateTime;

/**
 * 操作审计日志切面：拦截 @OperationLog 注解的方法，
 * 记录操作人、操作内容、参数、结果、耗时等信息到数据库。
 *
 * <p>使用 @Around 环绕通知，捕获正常返回和异常两种情况。
 * 异常不吞掉，记录日志后继续抛出。
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 拦截带有 @OperationLog 注解的方法。
     */
    @Around("@annotation(operationLogAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLogAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        HttpServletRequest request = getRequest();
        String requestMethod = request != null ? request.getMethod() : null;
        String requestUri = request != null ? request.getRequestURI() : null;
        String clientIp = request != null ? IpUtil.getClientIp(request) : null;

        // 获取当前用户（可能为 null，如注册接口无认证）
        Long operatorId = null;
        String operatorEmail = null;
        String operatorRoleKey = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            operatorId = loginUser.getUserId();
            operatorEmail = loginUser.getEmail();
            operatorRoleKey = loginUser.getRoleKey();
        }

        // 解析 SpEL 表达式获取目标对象 ID（不写 targetId 时为空）
        String targetId = resolveTargetId(operationLogAnnotation.targetId(), joinPoint);

        // 提取并脱敏请求参数
        String requestParams = extractParams(joinPoint);

        // 构造操作日志实体（公共部分）
        // 使用全限定名避免跟同名注解 OperationLog 冲突
        com.eldercare.system.entity.OperationLog entity = new com.eldercare.system.entity.OperationLog();
        entity.setModule(operationLogAnnotation.module());
        entity.setOperation(operationLogAnnotation.operation());
        entity.setActionType(operationLogAnnotation.actionType());
        entity.setRequestMethod(requestMethod);
        entity.setRequestUri(requestUri);
        entity.setOperatorId(operatorId);
        entity.setOperatorEmail(operatorEmail);
        entity.setOperatorRoleKey(operatorRoleKey);
        entity.setTargetId(targetId);
        entity.setRequestParams(requestParams);
        entity.setClientIp(clientIp);
        entity.setCreateTime(LocalDateTime.now());

        try {
            // 执行业务方法
            Object result = joinPoint.proceed();
            long costMs = System.currentTimeMillis() - startTime;

            // 解析返回结果：只有 code==200 才是真正成功
            if (result instanceof ApiResult<?> apiResult) {
                int code = apiResult.getCode() != null ? apiResult.getCode() : 500;
                entity.setResultCode(code);
                entity.setResultMessage(apiResult.getMessage());
                entity.setSuccess(code == 200 ? 1 : 0);
                // 非 200 时把返回消息写入失败原因，方便排查
                if (code != 200 && apiResult.getMessage() != null) {
                    entity.setErrorMessage(apiResult.getMessage());
                }
            } else {
                entity.setSuccess(1);
            }
            entity.setCostMs(costMs);

            // 异步保存
            operationLogService.saveAsync(entity);

            return result;

        } catch (Throwable e) {
            long costMs = System.currentTimeMillis() - startTime;

            entity.setSuccess(0);
            String errMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            entity.setErrorMessage(errMsg.length() > 500 ? errMsg.substring(0, 500) : errMsg);
            entity.setCostMs(costMs);

            // 异步保存失败日志
            operationLogService.saveAsync(entity);

            // 继续抛出异常，不吞掉
            throw e;
        }
    }

    /** SpEL 表达式解析器（静态单例，避免每次请求重复创建） */
    private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

    /**
     * 使用 SpEL 表达式从方法参数中获取目标对象 ID。
     *
     * <p>示例：
     * <ul>
     *   <li>{@code #userId} — 简单 @RequestParam/@PathVariable</li>
     *   <li>{@code #param.customerId} — @RequestBody DTO 字段</li>
     *   <li>{@code #body['customerId']} — Map 参数</li>
     * </ul>
     * 表达式为空或解析失败时返回 null，不影响主流程。
     */
    private String resolveTargetId(String expression, ProceedingJoinPoint joinPoint) {
        if (expression == null || expression.isBlank()) {
            return null;
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            // 编译可能未保留参数名，加一层空值保护
            if (paramNames == null || paramNames.length == 0) {
                return null;
            }

            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < paramNames.length && i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }

            Object value = SPEL_PARSER.parseExpression(expression).getValue(context);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("targetId SpEL 解析失败: expression={}, error={}", expression, e.getMessage());
            return null;
        }
    }

    /**
     * 提取并脱敏请求参数，文件上传只记录元信息。
     */
    private String extractParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "{}";
        }

        // 文件上传：只记录元信息
        if (SensitiveDataUtil.isFileUpload(args)) {
            return SensitiveDataUtil.extractFileInfo(args);
        }

        // 尝试将参数序列化为 JSON 并脱敏
        try {
            if (args.length == 1 && args[0] != null) {
                String json = JSON.toJSONString(args[0]);
                return SensitiveDataUtil.maskSensitive(json);
            }
            // 多个参数，构造键值对
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Parameter[] parameters = signature.getMethod().getParameters();
            JSONObject paramsObj = new JSONObject();
            for (int i = 0; i < parameters.length && i < args.length; i++) {
                if (args[i] != null) {
                    paramsObj.put(parameters[i].getName(), args[i]);
                }
            }
            return SensitiveDataUtil.maskSensitive(paramsObj.toJSONString());
        } catch (Exception e) {
            return "{\"_error\":\"param serialization failed\"}";
        }
    }

    /**
     * 获取当前 HTTP 请求。
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
