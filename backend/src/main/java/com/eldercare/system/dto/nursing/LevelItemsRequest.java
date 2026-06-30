package com.eldercare.system.dto.nursing;

import lombok.Data;

import java.util.List;

/**
 * 护理级别项目集合请求
 */
@Data
public class LevelItemsRequest {

    private Long levelId; // 护理级别 ID
    private List<Long> itemIds; // 护理项目 ID 列表
}
