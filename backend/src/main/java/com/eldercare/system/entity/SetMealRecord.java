package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 套餐记录
 */
@TableName("set_meal_record")
@Data
public class SetMealRecord extends BaseEntity {

    /** 套餐记录ID */
    @TableId(value = "set_meal_record_id", type = IdType.AUTO)
    private Long setMealRecordId;

    /** 套餐ID */
    private Long setMealId;

    /** 时段：0早餐 1午餐 2晚餐 */
    private String time;
}
