package com.eldercare.system.po.customer.customerparams;

import lombok.Data;

@Data
public class BuyItemRequest {
    private Long customerId;
    private Long itemId;
    private Integer buyCount;
    private String buyDate;   // "YYYY-MM-DD"
    private String expireDate; // "YYYY-MM-DD"

}
