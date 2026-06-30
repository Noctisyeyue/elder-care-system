package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

/**
 * 膳食日历保存请求
 */
@Data
public class SaveDietCalendarRequest {

    private String date; // 膳食日期
    private List<Long> breakfast; // 早餐菜品 ID 列表
    private List<Long> lunch; // 午餐菜品 ID 列表
    private List<Long> dinner; // 晚餐菜品 ID 列表
}
