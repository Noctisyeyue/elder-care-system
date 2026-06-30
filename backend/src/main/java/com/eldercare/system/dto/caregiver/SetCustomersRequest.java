package com.eldercare.system.dto.caregiver;

import lombok.Data;

import java.util.List;

/**
 * 设置健康管家服务对象请求
 */
@Data
public class SetCustomersRequest {

    Long caregiverId; // 健康管家用户 ID
    List<Long> customerIds; // 客户 ID 列表
}
