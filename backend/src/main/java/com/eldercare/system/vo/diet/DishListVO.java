package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

/**
 * 菜品列表视图
 */
@Data
public class DishListVO {

    private List<DishVO> list; // 菜品列表
    private Integer total; // 总记录数
}
