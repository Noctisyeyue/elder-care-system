package com.eldercare.system.dto.user;

import lombok.Data;

@Data
public class UserAddRequest {
    private String userName;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String gender;
    private String role;
}
