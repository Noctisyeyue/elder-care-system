package com.eldercare.system.dto.user;

import lombok.Data;

/** 发送邮箱修改验证码请求 */
@Data
public class EmailChangeCodeRequest {
    private String newEmail;
}
