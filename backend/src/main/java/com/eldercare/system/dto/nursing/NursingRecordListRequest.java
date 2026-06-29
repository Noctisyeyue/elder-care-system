package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理记录列表查询请求
 */
@Data
public class NursingRecordListRequest {
    /*
    customerId: number    // 客户ID
    pageNum: number
    pageSize: number
    */
    private Long customerId;
    private Integer pageNum;
    private Integer pageSize;
}
