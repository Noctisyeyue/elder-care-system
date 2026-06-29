package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别列表查询请求
 */
@Data
public class LevelListRequest {
    private String status;
    private Integer pageNum;
    private Integer pageSize;
}
