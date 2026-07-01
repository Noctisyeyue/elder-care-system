package com.eldercare.system.security;

import com.eldercare.system.util.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 未认证处理器：token 缺失、过期、签名错误或 Redis 校验失败时返回 401。
 *
 * <p>不作为 @Component，由 SecurityConfig 手动 new 并注册。
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** JSON 序列化器 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 写入 401 统一响应
     *
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param authException 认证异常
     * @throws IOException 写入响应失败时抛出
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ApiResult<Void> result = ApiResult.fail(401, "未登录或登录状态已过期，请重新登录");
        String json = objectMapper.writeValueAsString(result);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }
}
