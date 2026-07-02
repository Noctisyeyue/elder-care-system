package com.eldercare.system.vo.diet;

import lombok.Data;

/**
 * 本周膳食配餐量统计视图
 */
@Data
public class WeeklyMealCountVO {

    private String date;     // 日期
    private String dayOfWeek; // 星期几
    private Long count;      // 该日配餐客户数量
}
