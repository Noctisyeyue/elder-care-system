package com.eldercare.system.config;

import com.eldercare.system.util.AesUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AES 加密配置属性。
 *
 * <p>与 JwtProperties 同款模式：从 yml 读取密钥，启动时注入到 AesUtil 静态工具类。
 * 密钥真值写在 application-local.yml（已 gitignore，不入 git）。
 */
@Component
@ConfigurationProperties(prefix = "elder-care.aes")
public class AesProperties {

    /** AES 加密密钥 */
    private String secretKey;

    /**
     * 启动时把密钥注入 AesUtil
     */
    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isBlank()) {
            AesUtil.init(secretKey);
        }
    }

    /**
     * 获取 AES 密钥
     *
     * @return AES 密钥
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置 AES 密钥
     *
     * @param secretKey AES 密钥
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
