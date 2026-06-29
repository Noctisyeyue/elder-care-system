package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDishListListVO {
    /*
    list: []  // 客户膳食列表
    total: number  // 总记录数
    */
    private List<CustomerDishListVO> list;
    private Integer total;
}
