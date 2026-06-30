package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 购买护理项目请求
 */
@Data
public class BuyItemRequest {

    private Long customerId; // 客户 ID
    private Long itemId; // 护理项目 ID
    private Integer buyCount; // 购买次数
    private String buyDate; // 购买日期
    private String expireDate; // 到期日期
}
