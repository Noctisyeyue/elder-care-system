package com.eldercare.system.po.user;

/**
 * 角色ID与角色名映射工具
 */
public class UserRoleSwitch {

    /**
     * 将角色ID映射为角色名
     *
     * @param roleId 角色ID（1=管理员，2=健康管家）
     * @return 角色名，未知角色返回 null
     */
    public static String mapRoleIdToName(int roleId) {
        return switch (roleId) {
            case 1 -> "管理员";
            case 2 -> "健康管家";
            default -> null;
        };
    }
}
