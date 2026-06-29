package com.eldercare.system.po.diet.params;

import lombok.Data;

@Data
public class DishListParams {
    /*
    name: string	膳食名称（模糊搜索，可选）
    pork: string	是否含猪肉（0-含猪肉，1-不含猪肉，可选）
    pageNum:	number	当前页码（从1开始）
    pageSize:	number	每页条数
    */
    private String name;
    private String pork;
    private Integer pageNum;
    private Integer pageSize;
}
