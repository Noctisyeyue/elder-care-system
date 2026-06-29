package com.eldercare.system.util;

public class UserRoleSwitch {
        public static String mapRoleIdToName(int roleId) {
            return switch (roleId) {
                case 1 -> "管理员";
                case 2 -> "护工";
                case 3 -> "医生";
                case 4 -> "护士";
                default -> null;
            };
        }
}
