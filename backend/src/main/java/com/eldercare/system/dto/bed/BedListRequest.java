package com.eldercare.system.dto.bed;

import lombok.Data;

/**
 * 床位列表查询请求
 */
@Data
public class BedListRequest {

    private String customerName; // 客户姓名，支持模糊查询
    private String checkInDate; // 入住日期
    private Integer history; // 使用状态：1 正在使用，0 使用历史
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
