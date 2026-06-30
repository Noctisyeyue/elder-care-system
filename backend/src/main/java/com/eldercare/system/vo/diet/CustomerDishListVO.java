package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

/**
 * 客户膳食视图
 */
@Data
public class CustomerDishListVO {

    private Long customerId; // 客户 ID
    private String customerName; // 客户姓名
    private Integer age; // 年龄
    private String gender; // 性别
    private String nation; // 民族
    private String bedNumber; // 床位号
    private List<DishVO> breakfast; // 早餐菜品列表
    private List<DishVO> lunch; // 午餐菜品列表
    private List<DishVO> dinner; // 晚餐菜品列表
}
