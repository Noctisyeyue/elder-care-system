package com.eldercare.system.email;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 邮箱校验
     *
     * @param email 邮箱
     * @return true or false
     */
    public static boolean checkEmail(String email) {
        String check = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    /**
     * 密码校验（长度 6-18，至少包含1个字母）
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        String regex = ".{6,}"; // 支持6位及以上任意字符
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * 随机生成六位数字验证码
     */
    public static String randomSixCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    /**
     * 随机生成加密盐（4位随机字母 + 4位固定特殊字符）
     */
    public static String randomEncryptedSalt() {
        return RandomStringUtils.randomAlphabetic(4) + "#!$@";
    }
}
