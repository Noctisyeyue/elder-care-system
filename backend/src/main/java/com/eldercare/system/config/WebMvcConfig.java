package com.eldercare.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置。
 *
 * <p>认证授权已迁移至 Spring Security（见 SecurityConfig），旧 JWTInterceptor 不再注册。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

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
}

