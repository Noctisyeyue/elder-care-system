package com.eldercare.system.config;

import com.eldercare.system.util.JWTUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 从 yml 读取 JWT 配置，启动时注入到 JWTUtil
 */
@Component
@ConfigurationProperties(prefix = "elder-care.jwt")
public class JwtProperties {

    /** 签名密钥 */
    private String secretKey;
    /** 过期时间（毫秒） */
    private Long ttl;

    @PostConstruct
    public void init() {
        if (secretKey != null && ttl != null) {
            JWTUtil.init(secretKey, ttl);
        }
    }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }
}
