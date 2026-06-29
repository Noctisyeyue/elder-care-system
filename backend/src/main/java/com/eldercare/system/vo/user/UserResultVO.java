package com.eldercare.system.vo.user;

import lombok.Data;

@Data
public class UserResultVO {
    private Long index;
    private String userName;
    private String realName;
    private String phone;
    private String email;
    private String gender;
    private String role;
}
