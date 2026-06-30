package com.eldercare.system.vo.user;

import lombok.Data;

/**
 * 角色数量统计视图
 */
@Data
public class RoleNumVO {

    private Long value; // 角色用户数量
    private String name; // 角色名称
}
