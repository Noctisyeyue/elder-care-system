package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 护理级别
 */
@TableName("nursing_level")
@Data
public class NursingLevel extends BaseEntity {

    /** 护理级别ID */
    @TableId(value = "nursing_level_id", type = IdType.AUTO)
    private Long nursingLevelId;

    /** 护理级别名称 */
    private String nursingLevelName;

    /** 状态：0启用 1停用 */
    private String status;
}
