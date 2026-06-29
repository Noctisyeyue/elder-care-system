package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色
 */
@TableName("role")
@Data
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity {

    /** 角色ID */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    /** 角色名 */
    private String roleName;

    /** 角色标识（英文） */
    private String roleKey;
}
