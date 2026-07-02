package com.eldercare.system.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

/**
 * 敏感数据脱敏工具。
 *
 * <p>用于操作审计日志中对请求参数进行脱敏处理，避免密码、验证码、手机号等
 * 敏感信息以明文形式存入数据库。
 */
public class SensitiveDataUtil {

    /** 需要脱敏的参数字段名 */
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "oldPassword", "newPassword", "confirmPassword",
            "code", "token", "Authorization",
            "file",
            "phone", "tel",
            "realName", "customerName"
    );

    /** 脱敏替换值 */
    private static final String MASK = "***";

    /**
     * 对请求参数 JSON 字符串中的敏感字段进行递归脱敏。
     *
     * <p>遍历整个 JSON 树（对象和数组），将所有命中的敏感字段值替换为 "***"。
     * 字段名匹配忽略大小写。
     *
     * @param jsonStr 原始参数 JSON 字符串
     * @return 脱敏后的 JSON 字符串
     */
    public static String maskSensitive(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) {
            return "{}";
        }
        try {
            Object parsed = JSON.parse(jsonStr);
            maskRecursive(parsed);
            return JSON.toJSONString(parsed);
        } catch (Exception e) {
            // 非 JSON 格式的参数（如 form-data），直接返回脱敏标识
            return "{\"_masked\":\"***\"}";
        }
    }

    /**
     * 递归遍历 JSON 对象/数组，对敏感字段进行脱敏。
     */
    private static void maskRecursive(Object node) {
        if (node instanceof JSONObject obj) {
            for (String key : obj.keySet()) {
                Object value = obj.get(key);
                if (isSensitiveField(key)) {
                    obj.put(key, MASK);
                } else if (value != null) {
                    maskRecursive(value);
                }
            }
        } else if (node instanceof JSONArray arr) {
            for (int i = 0; i < arr.size(); i++) {
                maskRecursive(arr.get(i));
            }
        }
    }

    /**
     * 判断字段名是否为敏感字段（忽略大小写）。
     */
    private static boolean isSensitiveField(String name) {
        if (name == null) return false;
        return SENSITIVE_FIELDS.stream().anyMatch(field -> field.equalsIgnoreCase(name));
    }

    /**
     * 判断请求是否为文件上传（参数中包含 MultipartFile）。
     * 文件上传只记录 fileName/fileSize/contentType，不记录内容。
     */
    public static boolean isFileUpload(Object[] args) {
        if (args == null) return false;
        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从参数数组中提取 MultipartFile 的元信息（不记录文件内容）。
     */
    public static String extractFileInfo(Object[] args) {
        if (args == null) return "{}";
        JSONObject info = new JSONObject();
        for (Object arg : args) {
            if (arg instanceof MultipartFile file) {
                info.put("fileName", file.getOriginalFilename());
                info.put("fileSize", file.getSize());
                info.put("contentType", file.getContentType());
            }
        }
        return info.toJSONString();
    }
}
