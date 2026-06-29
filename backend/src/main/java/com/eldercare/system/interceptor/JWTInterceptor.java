package com.eldercare.system.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.eldercare.system.service.RedisService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * JWT 鉴权拦截器：从请求头取 Bearer token，验证签名后与 Redis 比对，通过才放行
 */
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    private final RedisService redisService;

    public JWTInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 取出 Authorization 头，去掉 "Bearer " 前缀
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        ApiResult<Void> result;
        try {
            if (token != null) {
                Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
                String userName = claims.get("userName").asString();

                // 对比 Redis 中存储的 token，确保是最新签发的
                String redisToken = redisService.getToken(userName);
                if (redisToken != null && redisToken.equals(token)) {
                    return true;
                }
                result = ApiResult.fail(401, "token验证失败");
            } else {
                result = ApiResult.fail(401, "token为空，登录状态已过期，请重新登录");
            }
        } catch (JWTVerificationException e) {
            log.info("JWT签名验证失败");
            result = ApiResult.fail(401, "签名不一致，请重新登录");
        } catch (Exception e) {
            log.info("token无效");
            result = ApiResult.fail(401, "token无效，请重新登录");
        }

        String json = new ObjectMapper().writeValueAsString(result);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}
