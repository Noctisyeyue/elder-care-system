package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理项目列表查询请求
 */
@Data
public class ItemListRequest {
    /*
    status: '启用',      // ‘启用’或者停用’
    page: 1,            // 当前页码
    pageSize: 10,       // 每页条数
    name: ''      // 护理项目名称（可选，模糊搜索），若为空返回全部
    */
    private String status;
    private Integer pageNum;
    private Integer pageSize;
    private String name;
}
