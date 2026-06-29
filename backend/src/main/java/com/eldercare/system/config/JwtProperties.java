package com.eldercare.system.config;

import com.eldercare.system.util.JWTUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Component
@ConfigurationProperties(prefix = "elder-care.jwt")
public class JwtProperties {

    /** JWT 签名密钥 */
    private String secretKey;

    /** JWT 过期时间，单位毫秒 */
    private Long ttl;

    /**
     * 初始化 JWT 工具类配置
     */
    @PostConstruct
    public void init() {
        if (secretKey != null && ttl != null) {
            JWTUtil.init(secretKey, ttl);
        }
    }

    /**
     * 获取 JWT 签名密钥
     *
     * @return JWT 签名密钥
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置 JWT 签名密钥
     *
     * @param secretKey JWT 签名密钥
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 获取 JWT 过期时间
     *
     * @return JWT 过期时间，单位毫秒
     */
    public Long getTtl() {
        return ttl;
    }

    /**
     * 设置 JWT 过期时间
     *
     * @param ttl JWT 过期时间，单位毫秒
     */
    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
