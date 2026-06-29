package com.eldercare.system.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * JWT 令牌工具：签发、验证、解析 payload
 */
public class JWTUtil {

    private static String SECRET = "elder-care-system-jwt-secret-key-2026";
    private static long TTL = 72000000; // 默认 20 小时（毫秒）

    /**
     * 启动时从 yml 配置注入密钥和过期时间
     *
     * @param secret JWT 签名密钥
     * @param ttl    令牌过期时间（毫秒）
     */
    public static void init(String secret, long ttl) {
        SECRET = secret;
        TTL = ttl;
    }

    /**
     * 签发 JWT 令牌
     *
     * @param map payload 键值对（如 userName、roleKey 等）
     * @return JWT 字符串
     */
    public static String getToken(Map<String, String> map) {
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MILLISECOND, (int) TTL);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证令牌签名和有效期，失败抛 JWTVerificationException
     *
     * @param token JWT 字符串
     * @return 解码后的 JWT 对象
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }

    /**
     * 解析 token 中的 payload 数据
     *
     * @param token JWT 字符串
     * @return payload 键值对
     */
    public static Map<String, Claim> getPayloadFromToken(String token) {
        return verify(token).getClaims();
    }
}
