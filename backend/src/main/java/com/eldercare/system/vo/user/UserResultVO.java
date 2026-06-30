package com.eldercare.system.vo.user;

import lombok.Data;

/**
 * 用户结果视图
 */
@Data
public class UserResultVO {

    private Long index; // 序号
    private String userName; // 用户名
    private String realName; // 真实姓名
    private String phone; // 手机号
    private String email; // 邮箱
    private String gender; // 性别
    private String role; // 角色名称
}
