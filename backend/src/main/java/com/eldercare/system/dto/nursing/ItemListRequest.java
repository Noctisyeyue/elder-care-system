package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理项目列表查询请求
 */
@Data
public class ItemListRequest {

    private String status; // 护理项目状态
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
    private String name; // 护理项目名称，支持模糊查询
}
