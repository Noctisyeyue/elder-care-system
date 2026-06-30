package com.eldercare.system.dto.diet;

import lombok.Data;

/**
 * 菜品列表查询请求
 */
@Data
public class DishListRequest {

    private String name; // 菜品名称，支持模糊查询
    private String pork; // 是否含猪肉
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
