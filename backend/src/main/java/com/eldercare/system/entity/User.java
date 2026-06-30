package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户
 */
@TableName("user")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {

    /** 用户ID */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /** 角色ID */
    private Long roleId;

    /** 用户名 */
    private String userName;

    /** 密码 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 性别 */
    private String gender;

    /** 账号状态：0 待审核，1 启用，2 禁用 */
    private Integer status;
}
