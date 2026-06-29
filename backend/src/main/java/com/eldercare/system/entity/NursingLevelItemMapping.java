package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 护理级别与护理项目映射
 */
@TableName("nursing_level_item_mapping")
@Data
public class NursingLevelItemMapping extends BaseEntity {

    /** 映射记录ID */
    @TableId(value = "nursing_level_item_mapping_id", type = IdType.AUTO)
    private Long nursingLevelItemMappingId;

    /** 护理级别ID */
    private Long nursingLevelId;

    /** 护理项目ID */
    private Long nursingItemId;
}
