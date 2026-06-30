package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 退住申请请求
 */
@Data
public class CheckoutRequest {

    private Long customerId; // 客户 ID
    private String customerName; // 客户姓名
    private String checkOutDate; // 退住日期
    private String checkOutType; // 退住类型
    private String checkOutReason; // 退住原因
}
