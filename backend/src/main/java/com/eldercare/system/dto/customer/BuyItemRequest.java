package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 购买护理项目请求
 */
@Data
public class BuyItemRequest {
    /** 客户ID */
    private Long customerId;
    /** 护理项目ID */
    private Long itemId;
    /** 购买次数 */
    private Integer buyCount;
    /** 购买日期（YYYY-MM-DD） */
    private String buyDate;
    /** 到期日期（YYYY-MM-DD） */
    private String expireDate;
}
