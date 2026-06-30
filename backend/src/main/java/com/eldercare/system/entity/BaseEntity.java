package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类，所有数据库实体继承它，自动获得通用审计字段。
 * createTime/updateTime 由 MyBatis-Plus MetaObjectHandler 自动填充。
 */
@Data
public class BaseEntity {

    /** 0=正常 1=已删除 */
    private String delFlag;
    /** 创建人 */
    private String createBy;
    /** 创建时间（插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 最后修改人 */
    private String updateBy;
    /** 最后修改时间（插入和更新时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
