package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 客户护理级别项目列表查询请求
 */
@Data
public class CustomerLevelItemListRequest {
    /*
    customerId ： number   客户id
    name ：string 护理项目名称，模糊搜索
    pageNum : number
    pageSize : number
    */
    private Long customerId;
    private String name;
    private Integer pageNum;
    private Integer pageSize;
}
