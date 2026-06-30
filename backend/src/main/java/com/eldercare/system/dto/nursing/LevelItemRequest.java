package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别项目关联请求
 */
@Data
public class LevelItemRequest {

    private Long levelId; // 护理级别 ID
    private Long itemId; // 护理项目 ID
}
