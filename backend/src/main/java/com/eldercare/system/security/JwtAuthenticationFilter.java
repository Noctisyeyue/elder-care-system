package com.eldercare.system.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JWT 认证过滤器：解析 Bearer token、校验 Redis 最新 token、校验账号状态，
 * 通过后构造 Authentication 写入 SecurityContextHolder。
 *
 * <p>逻辑等价于旧 JWTInterceptor.preHandle()，迁移后由 SecurityFilterChain 统一调度。
 * 不加 @Component，由 SecurityConfig 手动 new 并注册，避免被 Servlet 容器重复注册。
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** 待审核账号（status=0）允许访问的接口白名单 */
    private static final Set<String> PENDING_REVIEW_WHITELIST = Set.of(
            "/user/email/get",
            "/user/avatar/get",
            "/user/avatar/upload",
            "/user/status/get"
    );

    /** Redis 服务，用于读取/清除服务端保存的登录令牌 */
    private final RedisService redisService;

    /**
     * 创建 JWT 认证过滤器
     *
     * @param redisService Redis 服务
     */
    public JwtAuthenticationFilter(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 解析并校验 token，通过后写入 SecurityContext；失败时不写入，交由 Security 链返回 401
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException 过滤器链异常
     * @throws IOException      读写异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 取出 Authorization 头，去掉 "Bearer " 前缀
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            if (token == null) {
                // 没有 token，不写入 SecurityContext，交由 EntryPoint 返回 401
                filterChain.doFilter(request, response);
                return;
            }
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            String email = claims.get("email").asString();

            // 对比 Redis 中存储的 token，确保是最新签发的
            String redisToken = redisService.getToken(email);
            if (redisToken == null || !redisToken.equals(token)) {
                // token 不一致，不写入 SecurityContext
                filterChain.doFilter(request, response);
                return;
            }

            // 账号状态校验：读取 JWT 中的 status
            int status = parseIntClaim(claims.get("status"));
            // status 缺失或格式异常，按非法 token 处理，不写入 SecurityContext
            if (status < 0) {
                filterChain.doFilter(request, response);
                return;
            }
            // status=2 账号已被禁用：已认证但被拒绝，返回 403，并清除 Redis token
            if (status == 2) {
                redisService.deleteToken(email);
                writeFail(response, 403, "账号已被禁用，请联系管理员");
                return;
            }

            // 待审核账号（status=0）只能访问白名单接口：越权访问返回 403
            String path = request.getServletPath();
            if (status == 0 && !PENDING_REVIEW_WHITELIST.contains(path)) {
                writeFail(response, 403, "账号待审核，暂无权限访问");
                return;
            }

            // 构造当前登录用户并写入 SecurityContext
            String roleKey = claims.get("roleKey").asString();
            LoginUser loginUser = new LoginUser(
                    parseLongClaim(claims.get("userId")),
                    email,
                    parseLongClaim(claims.get("roleId")),
                    roleKey,
                    status
            );
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + roleKey.toUpperCase())
            );
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
            log.info("JWT签名验证失败");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.info("token无效");
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    /** JSON 序列化器，用于写 403 响应体 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将错误结果以 JSON 写入响应并结束当前请求（用于已认证但状态/权限拒绝的 403 场景）。
     *
     * <p>认证失败（token 非法/缺失）不由此方法处理，而是不写入 SecurityContext，
     * 交由 AuthenticationEntryPoint 返回 401。
     *
     * @param response HTTP 响应
     * @param code     错误码
     * @param message  错误消息
     * @throws IOException 写入响应失败时抛出
     */
    private void writeFail(HttpServletResponse response, int code, String message) throws IOException {
        ApiResult<Void> result = ApiResult.fail(code, message);
        String json = objectMapper.writeValueAsString(result);
        response.setStatus(code == 401 ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }

    /**
     * 安全解析 JWT 中的整型 claim，缺失或格式异常时返回 -1（视为非法状态，不放行）
     *
     * @param claim JWT claim
     * @return 整型值，解析失败返回 -1
     */
    private int parseIntClaim(Claim claim) {
        if (claim == null || claim.asString() == null) {
            return -1;
        }
        try {
            return Integer.parseInt(claim.asString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 安全解析 JWT 中的长整型 claim，缺失或格式异常时返回 null
     *
     * @param claim JWT claim
     * @return 长整型值，解析失败返回 null
     */
    private Long parseLongClaim(Claim claim) {
        if (claim == null || claim.asString() == null) {
            return null;
        }
        try {
            return Long.parseLong(claim.asString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
