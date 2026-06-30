package com.eldercare.system.dto.caregiver;

import lombok.Data;

/**
 * 健康管家服务对象查询请求
 */
@Data
public class CustomersByCareIdRequest {

    private Long caregiverId; // 健康管家用户 ID，可为空
    private Integer pageNum; // 当前页码
    private Integer pageSize; // 每页数量
    private String customerName; // 客户姓名，支持模糊查询
}
