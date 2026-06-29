package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class CustomerLevelItemListParams {
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
