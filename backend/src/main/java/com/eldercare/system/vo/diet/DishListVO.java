package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

@Data
public class DishListVO {
    private List<DishVO> list;
    private Integer total;
}
