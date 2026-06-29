package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别项目列表查询请求
 */
@Data
public class LevelItemListRequest {
    private Long levelId;
    private Integer pageNum;
    private Integer pageSize;
}
