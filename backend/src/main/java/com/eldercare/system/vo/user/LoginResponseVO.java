package com.eldercare.system.vo.user;

import lombok.Data;

/**
 * 登录响应视图
 */
@Data
public class LoginResponseVO {

    /** 登录令牌 */
    private String token;

    /** 用户 ID */
    private Long userId;

    /** 邮箱 */
    private String email;

    /** 真实姓名 */
    private String realName;

    /** 角色 ID */
    private Long roleId;

    /** 角色标识 */
    private String roleKey;

    /** 角色名 */
    private String roleName;

    /** 账号状态：0 待审核，1 启用，2 禁用 */
    private Integer status;
}
