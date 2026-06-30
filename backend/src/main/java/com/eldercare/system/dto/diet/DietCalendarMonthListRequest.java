package com.eldercare.system.dto.diet;

import lombok.Data;

/**
 * 膳食日历月视图查询请求
 */
@Data
public class DietCalendarMonthListRequest {

    private Integer year; // 年份
    private Integer month; // 月份
}
