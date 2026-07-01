package com.eldercare.system.dto.user;

import lombok.Data;

/** 当前用户修改密码请求 */
@Data
public class UserPasswordUpdateRequest {
    /** 当前密码 */
    private String oldPassword;
    /** 新密码 */
    private String newPassword;
    /** 确认新密码 */
    private String confirmPassword;
}
