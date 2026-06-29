package com.eldercare.system.po.nursing.params;

import lombok.Data;

import java.util.List;

@Data
public class AddItemRecordsParams {
    /*
    customerId: number;  // 客户ID
    levelId: number;    // 护理级别ID
    items: List<ItemRecordAddParams>
    */
    private Long customerId;
    private Long levelId;
    private List<ItemRecordAddParams> items;
}
