package com.eldercare.system.po.user;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT基础信息配置
 */
@Component
@ConfigurationProperties(prefix = "costmaster.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String secretKey;
    private long ttl;


}
