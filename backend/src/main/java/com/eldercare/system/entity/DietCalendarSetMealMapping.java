package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 膳食日历套餐映射实体
 */
@TableName("diet_calendar_set_meal_mapping")
@Data
public class DietCalendarSetMealMapping extends BaseEntity {
    /** 映射记录ID */
    @TableId(value = "diet_calendar_set_meal_mapping_id", type = IdType.AUTO)
    private Long dietCalendarSetMealMappingId;
    /** 膳食日历ID */
    private Long dietCalendarId;
    /** 套餐ID */
    private Long setMealId;
}
