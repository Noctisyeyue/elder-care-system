package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

@Data
public class DailyDishRecordVO {
    private List<DishVO> breakfast;
    private List<DishVO> lunch;
    private List<DishVO> dinner;
}
