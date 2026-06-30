package com.eldercare.system.dto.user;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest {

    private String userName; // 用户名
    private String password; // 密码
    private String role; // 登录角色标识

    /**
     * 获取角色编号
     *
     * @return 角色编号
     */
    public int getRoleId() {
        if (role.equals("admin")) {
            return 1;
        } else if (role.equals("caregiver")) {
            return 2;
        } else {
            return 0;
        }
    }
}
