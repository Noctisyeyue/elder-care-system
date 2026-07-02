package com.eldercare.system.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端 IP 获取工具。
 *
 * <p>优先从代理头读取真实 IP，兜底使用 request.getRemoteAddr()。
 * 适用于 Nginx/负载均衡等反向代理部署场景。
 */
public class IpUtil {

    /**
     * 获取客户端真实 IP。
     *
     * <p>读取顺序：X-Forwarded-For → X-Real-IP → Proxy-Client-IP
     * → WL-Proxy-Client-IP → RemoteAddr。
     * 如果存在多个 IP（逗号分隔），取第一个。
     *
     * @param request HTTP 请求
     * @return 客户端 IP，获取不到返回 "unknown"
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isValid(ip)) {
            // 多级代理时取第一个 IP
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (isValid(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (isValid(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValid(ip)) {
            return ip.trim();
        }

        ip = request.getRemoteAddr();
        return ip != null ? ip : "unknown";
    }

    /**
     * 判断 IP 字符串是否有效（非空且不是 unknown）。
     */
    private static boolean isValid(String ip) {
        return ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip);
    }
}
