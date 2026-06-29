package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

@Data
public class BatchSetCustomerDailyDietRecordsRequest {
    /*
    date: string	日期（格式如：2024-06-01）
    customerIds: number[]	客户ID数组
    breakfast: []	早餐菜品数组（与packageOfDay返回结构一致）
    lunch: []	午餐菜品数组
    dinner: []	晚餐菜品数组
    */
    private String date;
    private List<Long> customerIds;
    private List<DishRequest> breakfast;
    private List<DishRequest> lunch;
    private List<DishRequest> dinner;
}
