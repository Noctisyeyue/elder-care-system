package com.eldercare.system.dto.user;

import lombok.Data;

/** 当前用户资料更新请求（仅允许修改自身基本信息） */
@Data
public class UserProfileUpdateRequest {
    private String realName;
    private String phone;
    private String email;
    private String gender;
}
