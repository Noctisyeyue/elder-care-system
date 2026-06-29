package com.eldercare.system.dto.nursing;

import lombok.Data;
import java.util.List;

/**
 * 护理级别项目集合请求
 */
@Data
public class LevelItemsRequest {
    /*
    levelId: 1,      // 当前护理级别ID
    itemId: [2,3]     // 护理项目ID数组
    */
    private Long levelId;
    private List<Long> itemIds;
}
