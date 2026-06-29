package com.eldercare.system.config;

import com.eldercare.system.interceptor.JWTInterceptor;
import com.eldercare.system.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置 + JWT 拦截器注册
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** Redis 服务，用于 JWT 拦截器校验登录令牌 */
    @Autowired
    private RedisService redisService;

    /**
     * 允许所有来源跨域请求，允许携带 Cookie
     *
     * @param registry CORS 配置注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(36000);
    }

    /**
     * 注册 JWT 拦截器，拦截所有请求，排除登录、验证码、Swagger 等公开接口
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor(redisService))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/common/code/request",
                        "/common/code/email",
                        "/common/code/findPassword",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/doc.html",
                        "/favicon.ico"
                );
    }
}
