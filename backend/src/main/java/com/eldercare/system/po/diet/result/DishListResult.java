package com.eldercare.system.po.diet.result;

import lombok.Data;

import java.util.List;

@Data
public class DishListResult {
    private List<DishResult> list;
    private Integer total;
}
