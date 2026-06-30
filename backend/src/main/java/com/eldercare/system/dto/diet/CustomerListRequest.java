package com.eldercare.system.dto.diet;

import lombok.Data;

/**
 * 膳食客户列表查询请求
 */
@Data
public class CustomerListRequest {

    private String date; // 膳食日期
    private Integer pork; // 猪肉偏好标识
    private String customerName; // 客户姓名，支持模糊查询
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
