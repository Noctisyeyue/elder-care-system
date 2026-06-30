package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

/**
 * 客户套餐保存请求
 */
@Data
public class SaveCustomerSetMealRequest {

    private Long setMealId; // 套餐 ID
    private List<Long> customerIds; // 客户 ID 列表
    private String date; // 膳食日期
}
