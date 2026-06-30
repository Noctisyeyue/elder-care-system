package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

/**
 * 每日菜品记录视图
 */
@Data
public class DailyDishRecordVO {

    private List<DishVO> breakfast; // 早餐菜品列表
    private List<DishVO> lunch; // 午餐菜品列表
    private List<DishVO> dinner; // 晚餐菜品列表
}
