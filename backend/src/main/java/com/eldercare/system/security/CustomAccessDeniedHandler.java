package com.eldercare.system.security;

import com.eldercare.system.util.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 权限不足处理器：已登录但角色不匹配（含待审核/禁用账号越权访问）时返回 403。
 *
 * <p>不作为 @Component，由 SecurityConfig 手动 new 并注册。
 */
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 写入 403 统一响应
     *
     * @param request               HTTP 请求
     * @param response              HTTP 响应
     * @param accessDeniedException 权限异常
     * @throws IOException 写入响应失败时抛出
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ApiResult<Void> result = ApiResult.fail(403, "权限不足");
        String json = objectMapper.writeValueAsString(result);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }
}
