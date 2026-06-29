package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class ItemRecordAddParams {
    /*
    itemId: number;   // 项目ID
    buyDate: string;   // 购买日期，格式：YYYY-MM-DD
    buyCount: number;  // 购买数量
    expireDate: string; // 到期日期，格式：YYYY-MM-DD
    */
    private Long itemId;
    private String buyDate;
    private Integer buyCount;
    private String expireDate;
}
