package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 新增护理项目执行记录请求
 */
@Data
public class ItemRecordAddRequest {
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
