package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 外出申请请求
 */
@Data
public class OutingRequest {
    /*"customerId": 1,
  "customerName": "李福星",
  "outingReason": "去医院复查，需要做CT检查",  //外出事由，必填
  "outingDate": "2024-01-15",  // 外出时间，必填
  "returnDate": "2024-01-15",    // 预计回院时间，必填
  "companion": "小小立",    // 陪同人，可能为空
  "relationship": "弟弟",   // 关系，可能为空
  "companionPhone": "13912345678",  // 陪同人电话，可能为空*/
    Long customerId;
    String customerName;
    String outingReason;
    String outingDate;
    String returnDate;
    String companion;
    String relationship;
    String companionPhone;
}
