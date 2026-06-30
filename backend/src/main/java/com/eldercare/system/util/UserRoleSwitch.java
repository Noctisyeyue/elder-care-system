package com.eldercare.system.util;

import com.eldercare.system.constant.UserRoleConstant;

/**
 * 角色映射工具，仅保留三角色映射
 */
public class UserRoleSwitch {

    /**
     * 根据角色 ID 返回角色名
     *
     * @param roleId 角色 ID
     * @return 角色名，未匹配返回 null
     */
    public static String mapRoleIdToName(Long roleId) {
        if (UserRoleConstant.SUPER_ADMIN_ID.equals(roleId)) {
            return "超级管理员";
        }
        if (UserRoleConstant.ADMIN_ID.equals(roleId)) {
            return "管理员";
        }
        if (UserRoleConstant.CAREGIVER_ID.equals(roleId)) {
            return "护工";
        }
        return null;
    }
}
