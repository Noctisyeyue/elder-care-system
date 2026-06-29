package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

@Data
public class SaveCustomerDietRecordRequest {
    /*
    customerId: number,  // 客户ID
    date: string,       // 日期，格式：YYYY-MM-DD
    breakfast: []        // 早餐列表
    lunch: []        // 午餐列表
    dinner: []        // 晚餐列表
    */
    private Long customerId;
    private String date;
    private List<DishRequest> breakfast;
    private List<DishRequest> lunch;
    private List<DishRequest> dinner;
}
