package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

/**
 * 批量设置客户每日膳食记录请求
 */
@Data
public class BatchSetCustomerDailyDietRecordsRequest {

    private String date; // 膳食日期
    private List<Long> customerIds; // 客户 ID 列表
    private List<DishRequest> breakfast; // 早餐菜品列表
    private List<DishRequest> lunch; // 午餐菜品列表
    private List<DishRequest> dinner; // 晚餐菜品列表
}
