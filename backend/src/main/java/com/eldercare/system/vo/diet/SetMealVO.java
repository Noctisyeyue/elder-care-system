package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

/**
 * 套餐视图
 */
@Data
public class SetMealVO {

    private Long setMealId; // 套餐 ID
    private String name; // 套餐名称
    private String pork; // 是否含猪肉
    private String status; // 套餐状态
    private List<DishVO> breakfast; // 早餐菜品列表
    private List<DishVO> lunch; // 午餐菜品列表
    private List<DishVO> dinner; // 晚餐菜品列表
}
