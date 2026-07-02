package com.eldercare.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证码配置，映射 application.yml 中 elder-care.captcha 下的配置项。
 */
@Data
@ConfigurationProperties(prefix = "elder-care.captcha")
public class CaptchaProperties {

    /** 是否开启登录验证码，默认 true */
    private Boolean enabled = true;

    /** 验证码类型，当前只实现 math */
    private String type = "math";

    /** 验证码 Redis 过期时间（秒），默认 120 */
    private Integer expireSeconds = 120;

    /** 验证码图片宽度，默认 120 */
    private Integer width = 120;

    /** 验证码图片高度，默认 44 */
    private Integer height = 44;
}
