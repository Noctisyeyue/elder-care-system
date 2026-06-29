package com.eldercare.system.vo.caregiver;

import lombok.Data;

import java.util.List;

@Data
/** PurchasedItemsList视图 */
public class PurchasedItemsListVO {
    private List<PurchasedItemsVO> list;
    private int total;
    public PurchasedItemsListVO(List<PurchasedItemsVO> list, int total){
        this.list = list;
        this.total = total;
    }
}
