package com.eldercare.system.po.diet.params;

import lombok.Data;

@Data
public class DietCalendarSaveParams {
    private String date;
    private Long huiSetMealId;
    private Long notHuiSetMealId;
}
