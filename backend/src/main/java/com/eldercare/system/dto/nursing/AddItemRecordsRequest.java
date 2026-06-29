package com.eldercare.system.dto.nursing;

import lombok.Data;
import java.util.List;

/**
 * 新增护理项目记录请求
 */
@Data
public class AddItemRecordsRequest {
    /*
    customerId: number;  // 客户ID
    levelId: number;    // 护理级别ID
    items: List<ItemRecordAddRequest>
    */
    private Long customerId;
    private Long levelId;
    private List<ItemRecordAddRequest> items;
}
