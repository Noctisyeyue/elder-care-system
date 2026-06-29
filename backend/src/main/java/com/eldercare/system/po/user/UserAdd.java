package com.eldercare.system.po.user;

import lombok.Data;

@Data
public class UserAdd {
    private String userName;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String gender;
    private String role;
}
