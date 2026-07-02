package com.eldercare.system.dto.user;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest {

    /** 登录账号，可为用户名或邮箱 */
    private String account;

    /** 密码 */
    private String password;

    /** 验证码 */
    private String code;

    /** 验证码唯一标识 */
    private String uuid;
}
