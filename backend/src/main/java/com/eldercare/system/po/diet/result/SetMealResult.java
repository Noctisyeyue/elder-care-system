package com.eldercare.system.po.diet.result;

import lombok.Data;

import java.util.List;

@Data
public class SetMealResult {
    private Long setMealId;
    private String name;
    private String pork;
    private String status;
    private List<DishResult> breakfast;
    private List<DishResult> lunch;
    private List<DishResult> dinner;
}
