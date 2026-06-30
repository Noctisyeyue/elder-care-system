package com.eldercare.system.dto.diet;

import lombok.Data;

/**
 * 菜品保存请求
 */
@Data
public class DishRequest {

    private Long dishId; // 菜品 ID
    private String name; // 菜品名称
    private String category; // 菜品品类
    private String pork; // 是否含猪肉
    private String time; // 用餐时段
    private String status; // 菜品状态
}
