package com.eldercare.system.email;

public interface RedisConstant {

    // Key
    String EMAIL = "EMAIL_"; // 邮箱缓存前缀
    String EMAIL_REQUEST_VERIFY = "EMAIL_REQUEST_VERIFY_"; // 邮箱请求的权限码
    String REGISTER_CODE = "REGISTER:CODE:"; // 注册验证码前缀
    String REGISTER_CODE_LIMIT = "REGISTER:CODE:LIMIT:"; // 注册验证码发送间隔防刷前缀
    String EMAIL_CHANGE_CODE = "USER:EMAIL_CHANGE:CODE:"; // 邮箱修改验证码前缀
    String EMAIL_CHANGE_LIMIT = "USER:EMAIL_CHANGE:LIMIT:"; // 邮箱修改验证码发送频率限制（按用户，60 秒）

    // 缓存时间
    int EXPIRE_TEN_SECOND = 10; // 10s
    int EXPIRE_ONE_MINUTE = 60; // 1分钟
    int EXPIRE_FIVE_MINUTE = 5 * 60; // （五分钟）
    int EXPIRE_HALF_HOUR = 30 * 60; // 半小时（30分钟）
    int EXPIRE_ONE_DAY = 24 * 60 * 60; // （1天）
}

