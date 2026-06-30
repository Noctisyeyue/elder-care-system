package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 客户护理级别项目列表查询请求
 */
@Data
public class CustomerLevelItemListRequest {

    private Long customerId; // 客户 ID
    private String name; // 护理项目名称，支持模糊查询
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
