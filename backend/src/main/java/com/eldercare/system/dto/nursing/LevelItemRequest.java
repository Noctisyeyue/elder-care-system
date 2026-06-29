package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别项目关联请求
 */
@Data
public class LevelItemRequest {
    /*
    levelId: 1,      // 当前护理级别ID
    itemId: 2     // 护理项目ID
    */
    private Long levelId;
    private Long itemId;
}
