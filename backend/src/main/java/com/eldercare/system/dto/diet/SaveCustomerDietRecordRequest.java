package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

/**
 * 客户每日膳食记录保存请求
 */
@Data
public class SaveCustomerDietRecordRequest {

    private Long customerId; // 客户 ID
    private String date; // 膳食日期
    private List<DishRequest> breakfast; // 早餐菜品列表
    private List<DishRequest> lunch; // 午餐菜品列表
    private List<DishRequest> dinner; // 晚餐菜品列表
}
