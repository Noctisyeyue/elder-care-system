package com.eldercare.system.po.diet.result;

import lombok.Data;

import java.util.List;

@Data
public class DietCalendarResult {
    private Long calendarId;
    private String date;
    private List<SetMealResult> setMeals;
}
