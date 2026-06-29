package com.eldercare.system.dto.diet;

import lombok.Data;

@Data
public class DietCalendarSaveRequest {
    private String date;
    private Long huiSetMealId;
    private Long notHuiSetMealId;
}
