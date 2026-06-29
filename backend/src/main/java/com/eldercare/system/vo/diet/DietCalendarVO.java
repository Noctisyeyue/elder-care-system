package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

@Data
public class DietCalendarVO {
    private Long calendarId;
    private String date;
    private List<SetMealVO> setMeals;
}
