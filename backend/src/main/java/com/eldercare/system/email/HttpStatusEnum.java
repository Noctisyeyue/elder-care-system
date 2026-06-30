package com.eldercare.system.email;

import lombok.Getter;

@Getter
public enum HttpStatusEnum {

    EMAIL_ERROR(4001, "邮箱格式不正确"),
    PARAM_ERROR(4002, "参数格式不正确"),
    CODE_ERROR(4002, "验证码不正确"),
    PASSWORD_ERROR(4003, "密码错误"),
    USER_NOT_EXIST(4004, "用户不存在"),
    EMAIL_ALREADY_EXIST(4005, "邮箱不存在"),
    PASSWORD_INCONSISTENT(4006, "密码不一致"),
    PARAM_ILLEGAL(4007, "参数不合法"),
    EMAIL_REGISTERED(4008, "邮箱已被注册"),
    CODE_SEND_TOO_FREQUENT(4009, "验证码发送过于频繁，请稍后再试"),

    INTERNAL_SERVER_ERROR(500, "服务器异常"),
    UNKNOWN_ERROR(66666, "未知异常, 联系管理员"),
    ILLEGAL_OPERATION(88888, "非法操作");

    private final int code;
    private final String msg;

    HttpStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

