package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别列表查询请求
 */
@Data
public class LevelListRequest {

    private String status; // 护理级别状态
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
}
