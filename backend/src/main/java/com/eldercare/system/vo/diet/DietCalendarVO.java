package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

/**
 * 膳食日历视图
 */
@Data
public class DietCalendarVO {

    private Long calendarId; // 膳食日历 ID
    private String date; // 膳食日期
    private List<SetMealVO> setMeals; // 套餐列表
}
