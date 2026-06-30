package com.eldercare.system.dto.user;

import lombok.Data;

/**
 * 用户新增请求
 */
@Data
public class UserAddRequest {

    private String userName; // 用户名
    private String password; // 密码
    private String realName; // 真实姓名
    private String phone; // 手机号
    private String email; // 邮箱
    private String gender; // 性别
    private String role; // 角色标识
}
