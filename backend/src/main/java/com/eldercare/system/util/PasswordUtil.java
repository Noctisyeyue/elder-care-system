package com.eldercare.system.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // 加密
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // 验证
    public static boolean checkPassword(String password, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2")) {
            throw new IllegalArgumentException("Invalid hash format");
        }
        return BCrypt.checkpw(password, hashedPassword);
    }
}
