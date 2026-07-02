package com.eldercare.system.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * AES 可逆加密工具：用于敏感个人信息（如身份证号）的存储加密。
 *
 * <p>采用 AES/CBC/PKCS5Padding，固定 IV（保证相同明文→相同密文，便于脱敏/对齐，
 * 安全性远强于明文存储）。密钥从 application-local.yml 注入，不硬编码、不入 git。
 *
 * <p>参考国标 GB/T 35273「敏感个人信息需加密保存」、阿里云数据安全中心列加密方案。
 *
 * <p>使用模式与 JWTUtil 一致：静态工具类，启动时由 AesProperties.init() 注入密钥。
 */
public class AesUtil {

    /** AES 算法/模式/填充 */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /** 固定 IV（必须正好 16 字节）。固定 IV 保证相同明文加密结果一致，便于排查对齐。 */
    private static final byte[] IV = "elder-care-iv-16".getBytes(StandardCharsets.UTF_8);

    /** AES 密钥（启动时注入，未注入时为 null，调用加密会抛异常，避免静默使用弱默认值） */
    private static String SECRET = null;

    /**
     * 启动时从 yml 注入密钥
     *
     * @param secret AES 密钥
     */
    public static void init(String secret) {
        if (secret != null && !secret.isBlank()) {
            SECRET = secret;
        }
    }

    /**
     * 校验密钥已注入。未注入时抛异常，避免静默使用弱默认密钥导致安全风险。
     * 配置漏了 application-local.yml 的 elder-care.aes.secret-key 时，启动后第一次加解密即失败，问题尽早暴露。
     */
    private static void ensureKeyReady() {
        if (SECRET == null || SECRET.isBlank()) {
            throw new IllegalStateException(
                    "AES 密钥未配置，请在 application-local.yml 中设置 elder-care.aes.secret-key");
        }
    }

    /**
     * 加密明文，返回 Base64 字符串。
     *
     * @param plainText 明文
     * @return Base64 密文，输入为 null/空时原样返回
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        ensureKeyReady();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);                                // 创建加密器
            SecretKeySpec keySpec = new SecretKeySpec(deriveKey(SECRET), "AES");          // 准备钥匙
            IvParameterSpec ivSpec = new IvParameterSpec(IV);                             // 准备IV（见下文）
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);                            // 初始化为「加密模式」
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));// 执行加密
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * 解密 Base64 密文，返回明文。
     *
     * @param cipherText Base64 密文
     * @return 明文，输入为 null/空时原样返回
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        ensureKeyReady();
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(deriveKey(SECRET), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 安全解密：解密失败（历史明文、格式不符、密钥变更）时原样返回，不抛异常。
     *
     * <p>用于列表查询等"宁可显示明文也不能让整页崩溃"的场景。
     * 注意：返回原值意味着可能把明文暴露给前端，仅作为过渡兼容，最终应通过迁移脚本把所有数据加密。
     *
     * @param text 可能是密文，也可能是历史明文
     * @return 解密后的明文；若无法解密则原样返回
     */
    public static String decryptSafely(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        // 密钥缺失异常不能吞掉，必须在 try 外面抛出
        ensureKeyReady();
        try {
            return decrypt(text);
        } catch (Exception e) {
            // 仅吞「解密失败」类异常（历史明文、格式不符），密钥缺失已在上面独立处理
            return text;
        }
    }

    /**
     * 把任意长度密钥派生为 AES 要求的 16 字节（128 位）密钥。
     * 用 SHA-256 摘要后取前 16 字节，保证密钥长度合法。
     */
    private static byte[] deriveKey(String secret) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(secret.getBytes(StandardCharsets.UTF_8));
        byte[] key = new byte[16];
        System.arraycopy(hash, 0, key, 0, 16);
        return key;
    }
}
