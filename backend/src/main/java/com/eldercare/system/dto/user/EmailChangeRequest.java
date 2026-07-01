package com.eldercare.system.dto.user;

import lombok.Data;

/** 确认修改邮箱请求 */
@Data
public class EmailChangeRequest {
    private String newEmail;
    private String code;
}
