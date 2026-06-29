package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

@Data
public class SetMealVO {
    private Long setMealId;
    private String name;
    private String pork;
    private String status;
    private List<DishVO> breakfast;
    private List<DishVO> lunch;
    private List<DishVO> dinner;
}
