package com.eldercare.system.vo.diet;

import lombok.Data;

@Data
public class DishVO {
    private Long dishId;
    private String name;
    private String category;
    private String pork;
    private String time;
    private String status;
    private String img;
}
