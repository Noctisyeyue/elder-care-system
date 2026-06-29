package com.eldercare.system.po.diet.result;

import lombok.Data;

import java.util.List;

@Data
public class DailyDishRecordResult {
    private List<DishResult> breakfast;
    private List<DishResult> lunch;
    private List<DishResult> dinner;
}
