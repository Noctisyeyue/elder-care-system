package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 外出申请请求
 */
@Data
public class OutingRequest {

    Long customerId; // 客户 ID
    String customerName; // 客户姓名
    String outingReason; // 外出事由
    String outingDate; // 外出时间
    String returnDate; // 预计回院时间
    String companion; // 陪同人
    String relationship; // 陪同人与客户关系
    String companionPhone; // 陪同人电话
}
