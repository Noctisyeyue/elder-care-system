package com.eldercare.system.dto.caregiver;

import lombok.Data;

import java.util.List;

/**
 * 设置健康管家客户请求
 */
@Data
public class SetCustomersRequest {
    Long caregiverId;
    List<Long> customerIds;
}
