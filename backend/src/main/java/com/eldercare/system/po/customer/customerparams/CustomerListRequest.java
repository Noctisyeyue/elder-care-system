package com.eldercare.system.po.customer.customerparams;

import lombok.Data;

@Data
public class CustomerListRequest {
    private String customerName;      // 可为空
    private String customerType;      // 可为空，值应为 'self-care' 或 'nursing-care'
    private Integer pageNum;             // 必填
    private Integer pageSize;         // 必填
}