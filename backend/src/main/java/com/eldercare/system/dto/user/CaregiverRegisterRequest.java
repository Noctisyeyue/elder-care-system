package com.eldercare.system.dto.user;

import lombok.Data;

/**
 * 护工注册请求
 */
@Data
public class CaregiverRegisterRequest {

    /** 用户名，4-20 位字母/数字/下划线 */
    private String userName;

    /** 邮箱，用于接收验证码和登录 */
    private String email;

    /** 邮箱验证码 */
    private String code;

    /** 登录密码 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号，仅作联系方式留存 */
    private String phone;

    /** 性别 */
    private String gender;
}
