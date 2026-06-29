package com.eldercare.system.po.diet.params;

import lombok.Data;

import java.util.List;

@Data
public class SaveSetMealDishesParams {
    private Long setMealId;
    private List<Long> breakfastIds;
    private List<Long> lunchIds;
    private List<Long> dinnerIds;
}
