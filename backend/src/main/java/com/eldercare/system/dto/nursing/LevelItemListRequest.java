package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别项目列表查询请求
 */
@Data
public class LevelItemListRequest {

    private Long levelId; // 护理级别 ID
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
