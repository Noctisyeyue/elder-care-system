package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理记录列表查询请求
 */
@Data
public class NursingRecordListRequest {

    private Long customerId; // 客户 ID
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
