package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 套餐
 */
@TableName("set_meal")
@Data
public class SetMeal extends BaseEntity {

    /** 套餐ID */
    @TableId(value = "set_meal_id", type = IdType.AUTO)
    private Long setMealId;

    /** 套餐名 */
    private String setMealName;

    /** 是否含猪肉：0含有猪肉 1不含猪肉 */
    private String pork;

    /** 状态：0启用 1停用 */
    private String status;
}
