package com.eldercare.system.vo.user;

import lombok.Data;

/** 当前用户资料视图 */
@Data
public class UserProfileVO {
    private String userName;
    private String realName;
    private String phone;
    private String email;
    private String gender;
    private String roleName;
}
