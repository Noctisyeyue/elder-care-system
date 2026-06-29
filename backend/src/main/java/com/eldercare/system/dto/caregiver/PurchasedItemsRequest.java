package com.eldercare.system.dto.caregiver;

import lombok.Data;

@Data
/** PurchasedItems请求 */
public class PurchasedItemsRequest {
    private Long customerId;
    private Integer pageNum;
    private Integer pageSize;
    private String itemName;
}
