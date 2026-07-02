package com.eldercare.system.security;

import com.eldercare.system.util.IpUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * 请求访问日志过滤器：记录每次 HTTP 请求的方法、路径、状态码、耗时、IP 和当前用户。
 *
 * <p>放在 JwtAuthenticationFilter 之前，确保能覆盖 token 无效（401）、
 * 账号禁用/待审核越权（403）等认证阶段的响应。
 * 正常认证成功后，从 SecurityContextHolder 读取当前用户信息。
 *
 * <p>跳过 Swagger 文档、favicon 等非业务请求。
 */
@Slf4j
public class RequestLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 跳过非业务路径
        String path = request.getServletPath();
        if (shouldSkip(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 生成本次请求唯一标识（8 位，仅用于日志追踪）
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            long costMs = System.currentTimeMillis() - startTime;
            logRequest(requestId, request, status, costMs);
        }
    }

    /**
     * 判断是否跳过该路径（Swagger 文档、favicon 等非业务请求）。
     */
    private boolean shouldSkip(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || "/swagger-ui.html".equals(path)
                || "/doc.html".equals(path)
                || "/favicon.ico".equals(path);
    }

    /**
     * 输出请求日志到应用日志文件。
     */
    private void logRequest(String requestId, HttpServletRequest request, int status, long costMs) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = IpUtil.getClientIp(request);

        // 从 SecurityContextHolder 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        String email = null;
        String roleKey = null;

        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            userId = loginUser.getUserId();
            email = loginUser.getEmail();
            roleKey = loginUser.getRoleKey();
        }

        log.info("[请求-{}] 方法={} 路径={} | 查询参数={} | 状态码={} | 耗时={}ms | IP={} | 用户ID={} | 邮箱={} | 角色={}",
                requestId, method, uri,
                maskQueryString(queryString),
                status, costMs, clientIp,
                userId, email, roleKey);
    }

    /** 需要对查询参数中脱敏的 key */
    private static final Set<String> SENSITIVE_QS_KEYS = Set.of(
            "code", "token", "password", "oldPassword", "newPassword",
            "phone", "tel", "realName"
    );

    /**
     * 对查询字符串中的敏感参数值进行脱敏（如 code=123456 → code=***）。
     * key 匹配忽略大小写。
     */
    private String maskQueryString(String qs) {
        if (qs == null || qs.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        String[] pairs = qs.split("&");
        for (String pair : pairs) {
            if (!sb.isEmpty()) sb.append("&");
            int eq = pair.indexOf('=');
            String key = eq > 0 ? pair.substring(0, eq) : pair;
            boolean sensitive = SENSITIVE_QS_KEYS.stream().anyMatch(k -> k.equalsIgnoreCase(key));
            if (eq > 0 && sensitive) {
                sb.append(pair, 0, eq).append("=***");
            } else {
                sb.append(pair);
            }
        }
        return sb.toString();
    }
}
