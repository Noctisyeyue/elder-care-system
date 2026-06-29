package com.eldercare.system.po.caregiver.results;

import lombok.Data;

import java.util.List;

@Data
public class PurchasedItemsListResult {
    private List<PurchasedItemsResult> list;
    private int total;
    public PurchasedItemsListResult(List<PurchasedItemsResult> list, int total){
        this.list = list;
        this.total = total;
    }
}
