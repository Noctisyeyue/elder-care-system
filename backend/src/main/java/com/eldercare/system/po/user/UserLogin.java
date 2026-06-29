package com.eldercare.system.po.user;

import lombok.Data;
//用于接受用户登录信息
@Data
public class UserLogin {
    private String userName;
    private String password;
    private String role;
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
