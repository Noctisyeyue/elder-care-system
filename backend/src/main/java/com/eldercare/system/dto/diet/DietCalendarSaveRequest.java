package com.eldercare.system.dto.diet;

import lombok.Data;

/**
 * 膳食日历套餐保存请求
 */
@Data
public class DietCalendarSaveRequest {

    private String date; // 膳食日期
    private Long huiSetMealId; // 含猪肉套餐 ID
    private Long notHuiSetMealId; // 不含猪肉套餐 ID
}
