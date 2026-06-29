package com.eldercare.system.po.caregiver.params;

import lombok.Data;

@Data
public class PurchasedItemsRequest {
    private Long customerId;
    private Integer pageNum;
    private Integer pageSize;
    private String itemName;
}
