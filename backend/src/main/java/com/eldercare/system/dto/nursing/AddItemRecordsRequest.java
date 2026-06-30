package com.eldercare.system.dto.nursing;

import lombok.Data;

import java.util.List;

/**
 * 新增护理项目记录请求
 */
@Data
public class AddItemRecordsRequest {

    private Long customerId; // 客户 ID
    private Long levelId; // 护理级别 ID
    private List<ItemRecordAddRequest> items; // 护理项目记录列表
}
