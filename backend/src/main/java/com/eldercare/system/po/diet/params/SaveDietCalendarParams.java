package com.eldercare.system.po.diet.params;

import lombok.Data;

import java.util.List;

@Data
public class SaveDietCalendarParams {
    /*
    "date": "2024-06-01",           // 日期，字符串
    "breakfast": [1, 2],            // 早餐食品id数组
    "lunch": [3],                   // 午餐食品id数组
    "dinner": [4]                   // 晚餐食品id数组
    */
    private String date;
    private List<Long> breakfast;
    private List<Long> lunch;
    private List<Long> dinner;
}
