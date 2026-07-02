package com.eldercare.system.config;

import com.eldercare.system.security.CustomAccessDeniedHandler;
import com.eldercare.system.security.CustomAuthenticationEntryPoint;
import com.eldercare.system.security.JwtAuthenticationFilter;
import com.eldercare.system.security.RequestLogFilter;
import com.eldercare.system.service.RedisService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：替代旧 JWTInterceptor，负责过滤器注册、白名单放行、401/403 统一响应。
 *
 * <p>CORS 复用 WebMvcConfig 中已有的配置（.cors(withDefaults()) 自动接管）。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(CaptchaProperties.class)
public class SecurityConfig {

    /** Redis 服务，用于构造 JWT 过滤器 */
    private final RedisService redisService;

    /**
     * 创建 Security 配置
     *
     * @param redisService Redis 服务
     */
    public SecurityConfig(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 配置安全过滤链
     *
     * @param http HttpSecurity 构建器
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 前后端分离 + JWT，关闭 CSRF
                .csrf(csrf -> csrf.disable())
                // 无状态会话，不创建 HttpSession
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 复用 WebMvcConfig 的 CORS 配置（withDefaults 会自动使用 WebMvcConfigurer 注册的 mapping）
                .cors(Customizer.withDefaults())
                // 禁用默认表单登录和 BasicAuth（避免浏览器弹窗）
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 预检请求统一放行，避免无 token 被拦截
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 公开接口白名单（与 WebMvcConfig 保持一致）
                        .requestMatchers(
                                "/user/login",
                                "/user/register",
                                "/common/code/request",
                                "/common/code/email",
                                "/common/code/findPassword",
                                "/common/code/register",
                                "/common/captcha",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/doc.html",
                                "/favicon.ico"
                        ).permitAll()
                        // 其他接口需要登录
                        .anyRequest().authenticated()
                )
                // 注册 JWT 过滤器，在用户名密码过滤器之前
                .addFilterBefore(
                        new JwtAuthenticationFilter(redisService),
                        UsernamePasswordAuthenticationFilter.class
                )
                // 请求日志过滤器放在 JWT 过滤器之前，覆盖 401/403 等认证阶段响应
                .addFilterBefore(new RequestLogFilter(), JwtAuthenticationFilter.class)
                // 401/403 统一返回 ApiResult
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        return http.build();
    }
}
