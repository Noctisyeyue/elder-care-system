package com.eldercare.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * 颐养中心后台管理系统启动类。
 *
 * <p>排除 UserDetailsServiceAutoConfiguration：项目使用自定义 JWT 认证，不使用 Spring Security
 * 默认的内存用户（避免启动时生成随机密码 user/xxx 的告警）。
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@MapperScan("com.eldercare.system.mapper")
public class ElderCareSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderCareSystemApplication.class, args);
    }
}
