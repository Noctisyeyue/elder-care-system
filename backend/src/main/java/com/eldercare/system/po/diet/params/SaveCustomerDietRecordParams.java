package com.eldercare.system.po.diet.params;

import lombok.Data;

import java.util.List;

@Data
public class SaveCustomerDietRecordParams {
    /*
    customerId: number,  // 客户ID
    date: string,       // 日期，格式：YYYY-MM-DD
    breakfast: []        // 早餐列表
    lunch: []        // 午餐列表
    dinner: []        // 晚餐列表
    */
    private Long customerId;
    private String date;
    private List<DishParams> breakfast;
    private List<DishParams> lunch;
    private List<DishParams> dinner;
}
