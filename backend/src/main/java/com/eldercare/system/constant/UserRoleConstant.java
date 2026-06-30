package com.eldercare.system.constant;

/**
 * 用户角色常量，统一管理 role_id 和 role_key，避免代码中出现魔法数字
 */
public class UserRoleConstant {

    /** 超级管理员角色 ID */
    public static final Long SUPER_ADMIN_ID = 1L;
    /** 管理员角色 ID */
    public static final Long ADMIN_ID = 2L;
    /** 护工角色 ID */
    public static final Long CAREGIVER_ID = 3L;

    /** 超级管理员角色标识 */
    public static final String SUPER_ADMIN_KEY = "super_admin";
    /** 管理员角色标识 */
    public static final String ADMIN_KEY = "admin";
    /** 护工角色标识 */
    public static final String CAREGIVER_KEY = "caregiver";

    private UserRoleConstant() {
    }
}
