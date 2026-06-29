package com.eldercare.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务，用于 JWT 令牌的存取和校验
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 将 token 存入 Redis，key=用户名，value=token
     *
     * @param username   用户名
     * @param token      令牌
     * @param expireTime 过期时间（秒）
     */
    public void setToken(String username, String token, long expireTime) {
        redisTemplate.opsForValue().set(username, token, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 根据用户名取出 Redis 中的 token
     *
     * @param username 用户名
     * @return token 字符串，不存在则返回 null
     */
    public String getToken(String username) {
        return redisTemplate.opsForValue().get(username);
    }

    /**
     * 删除 token（退出登录时调用）
     *
     * @param username 用户名
     */
    public void deleteToken(String username) {
        redisTemplate.delete(username);
    }

    /**
     * 检查 key 是否存在于 Redis 中
     *
     * @param username 用户名
     * @return true=存在
     */
    public boolean hasToken(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(username));
    }
}
