package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

@Data
public class SaveSetMealDishesRequest {
    private Long setMealId;
    private List<Long> breakfastIds;
    private List<Long> lunchIds;
    private List<Long> dinnerIds;
}
