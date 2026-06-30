package com.eldercare.system.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.eldercare.system.constant.UserRoleConstant;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Set;

/**
 * JWT 鉴权拦截器：从请求头取 Bearer token，验证签名后与 Redis 比对，
 * 通过后再校验账号状态和角色权限
 */
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    /** Redis 服务，用于读取服务端保存的登录令牌 */
    private final RedisService redisService;

    /** 待审核账号（status=0）允许访问的接口白名单 */
    private static final Set<String> PENDING_REVIEW_WHITELIST = Set.of(
            "/user/email/get",
            "/user/avatar/get",
            "/user/avatar/upload"
    );

    /** 仅超级管理员可访问的接口（用户管理类） */
    private static final Set<String> SUPER_ADMIN_ONLY = Set.of(
            "/user/list",
            "/user/count",
            "/user/roleNum",
            "/user/add",
            "/user/update",
            "/user/del",
            "/user/audit",
            "/user/disable",
            "/user/resetPwd"
    );

    /**
     * 创建 JWT 鉴权拦截器
     *
     * @param redisService Redis 服务
     */
    public JWTInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 请求进入 Controller 前校验 JWT 令牌
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  当前处理器
     * @return true=校验通过，false=拦截请求
     * @throws Exception 写入响应失败时抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行 CORS 跨域预检请求（OPTIONS 不带 token，必须直接通过）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 取出 Authorization 头，去掉 "Bearer " 前缀
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            if (token == null) {
                return writeFail(response, 401, "token为空，登录状态已过期，请重新登录");
            }
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            String email = claims.get("email").asString();

            // 对比 Redis 中存储的 token，确保是最新签发的
            String redisToken = redisService.getToken(email);
            if (redisToken == null || !redisToken.equals(token)) {
                return writeFail(response, 401, "token验证失败");
            }

            // 账号状态校验：读取 JWT 中的 status
            int status = parseIntClaim(claims.get("status"));
            // status=2 账号已被禁用，拒绝并清除 Redis token
            if (status == 2) {
                redisService.deleteToken(email);
                return writeFail(response, 403, "账号已被禁用，请联系管理员");
            }

            // 待审核账号（status=0）只能访问白名单接口
            String path = request.getServletPath();
            if (status == 0 && !PENDING_REVIEW_WHITELIST.contains(path)) {
                return writeFail(response, 403, "账号待审核，暂无权限访问");
            }

            // 角色权限校验：用户管理类接口仅超级管理员可访问
            String roleKey = claims.get("roleKey").asString();
            if (SUPER_ADMIN_ONLY.contains(path)
                    && !UserRoleConstant.SUPER_ADMIN_KEY.equals(roleKey)) {
                return writeFail(response, 403, "权限不足，仅超级管理员可操作");
            }

            return true;
        } catch (JWTVerificationException e) {
            log.info("JWT签名验证失败");
            return writeFail(response, 401, "签名不一致，请重新登录");
        } catch (Exception e) {
            log.info("token无效");
            return writeFail(response, 401, "token无效，请重新登录");
        }
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
     * 将错误结果以 JSON 写入响应，返回 false 拦截请求
     *
     * @param response HTTP 响应
     * @param code     错误码
     * @param message  错误消息
     * @return false，表示拦截
     * @throws Exception 写入响应失败时抛出
     */
    private boolean writeFail(HttpServletResponse response, int code, String message) throws Exception {
        ApiResult<Void> result = ApiResult.fail(code, message);
        String json = new ObjectMapper().writeValueAsString(result);
        // 401 → 未认证；其他（403 等）→ 无权限
        response.setStatus(code == 401 ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
