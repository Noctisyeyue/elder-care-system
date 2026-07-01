package com.eldercare.system.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户信息，作为 SecurityContextHolder 中的 principal 数据对象。
 *
 * <p>第一阶段不实现 UserDetails，登录仍由 UserServiceImpl.login() + JWTUtil 完成，
 * 本类只用于过滤器解析 token 后携带用户信息，供业务层通过 SecurityContextHolder 获取。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    /** 用户 ID */
    private Long userId;

    /** 邮箱（Redis token key 使用） */
    private String email;

    /** 角色 ID */
    private Long roleId;

    /** 角色标识，如 super_admin */
    private String roleKey;

    /** 账号状态：0 待审核，1 启用，2 禁用 */
    private Integer status;
}
