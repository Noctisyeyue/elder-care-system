package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

/**
 * 套餐菜品保存请求
 */
@Data
public class SaveSetMealDishesRequest {

    private Long setMealId; // 套餐 ID
    private List<Long> breakfastIds; // 早餐菜品 ID 列表
    private List<Long> lunchIds; // 午餐菜品 ID 列表
    private List<Long> dinnerIds; // 晚餐菜品 ID 列表
}
