package com.eldercare.system.po.nursing.result;

import lombok.Data;

import java.util.List;

@Data
public class ItemListResult {
    /*
    "records"(ItemResult)
    "total": 2
    */
    private List<ItemResult> records;
    private Long total;
}
