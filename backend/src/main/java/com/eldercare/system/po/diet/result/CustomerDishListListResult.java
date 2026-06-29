package com.eldercare.system.po.diet.result;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDishListListResult {
    /*
    list: []  // 客户膳食列表
    total: number  // 总记录数
    */
    private List<CustomerDishListResult> list;
    private Integer total;
}
