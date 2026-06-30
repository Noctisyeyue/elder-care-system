package com.eldercare.system.dto.caregiver;

import lombok.Data;

/**
 * 已购买护理项目查询请求
 */
@Data
public class PurchasedItemsRequest {

    private Long customerId; // 客户 ID
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
    private String itemName; // 护理项目名称，支持模糊查询
}
