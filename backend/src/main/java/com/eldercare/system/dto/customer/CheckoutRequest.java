package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 退住申请请求
 */
@Data
public class CheckoutRequest {
    /*customerId	number		客户的唯一标识ID
    customerName	 string		客户姓名
    checkOutDate	 string		退住日期，格式: 'YYYY-MM-DD'
    checkOutType	 string		退住类型 ('正常退住', '死亡退住', '保留床位')
    checkOutReason	 string		退住的具体原因*/
    private Long customerId;
    private String customerName;
    private String checkOutDate;
    private String checkOutType;
    private String checkOutReason;
}
