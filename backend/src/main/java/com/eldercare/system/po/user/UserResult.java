package com.eldercare.system.po.user;

import lombok.Data;

@Data
public class UserResult {
    private Long index;
    private String userName;
    private String realName;
    private String phone;
    private String email;
    private String gender;
    private String role;
}
