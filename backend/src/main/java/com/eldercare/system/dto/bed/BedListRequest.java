package com.eldercare.system.dto.bed;

import lombok.Data;

@Data
public class BedListRequest {
    /*
    customerName: string, // 客户姓名 (模糊查询)
    checkInDate: string,  // 入住日期 (格式: YYYY-MM-DD)
    history: number,       // 使用状态 (1: 正在使用, 0: 使用历史)
    currentPage: number,  // 当前页码
    pageSize: number      // 每页大小
    */
    private String customerName;
    private String checkInDate;
    private Integer history;
    private Integer pageNum;
    private Integer pageSize;

}
