package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 菜品实体
 */
@TableName("dish")
@Data
public class Dish extends BaseEntity {
    /** 菜品ID */
    @TableId(value = "dish_id", type = IdType.AUTO)
    private Long dishId;
    /** 菜品名 */
    private String dishName;
    /** 菜品品类（0素菜 1荤菜 2主食 3饮品） */
    private String dishType;
    /** 状态（0启用 1停用） */
    private String status;
    /** 菜品时期（0早餐 1午餐 2晚餐） */
    private String time;
    /** 是否含猪肉（0含有 1不含） */
    private String pork;
    /** 菜品图片 */
    private String img;
}
