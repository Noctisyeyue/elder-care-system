package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 客户列表查询请求
 */
@Data
public class CustomerListRequest {

    private String customerName; // 客户姓名，支持模糊查询
    private String customerType; // 客户类型：self-care 自理老人，nursing-care 护理老人
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
