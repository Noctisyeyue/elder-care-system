package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 套餐记录菜品映射
 */
@TableName("set_meal_record_dish_mapping")
@Data
public class SetMealRecordDishMapping extends BaseEntity {

    /** 映射记录ID */
    @TableId(value = "set_meal_record_dish_mapping_id", type = IdType.AUTO)
    private Long setMealRecordDishMappingId;

    /** 套餐记录ID */
    private Long setMealRecordId;

    /** 菜品ID */
    private Long dishId;
}
