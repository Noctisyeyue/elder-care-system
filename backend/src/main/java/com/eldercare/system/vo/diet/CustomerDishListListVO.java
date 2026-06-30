package com.eldercare.system.vo.diet;

import lombok.Data;

import java.util.List;

/**
 * 客户膳食列表视图
 */
@Data
public class CustomerDishListListVO {

    private List<CustomerDishListVO> list; // 客户膳食列表
    private Integer total; // 总记录数
}
