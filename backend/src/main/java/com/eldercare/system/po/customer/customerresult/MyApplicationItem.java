package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

@Data
public class MyApplicationItem {
    /*"id": 1,
      "customerName": "李福星",
      "outingReason": "家属接回家过周末",
      "outingDate": "2025-06-21",
      "returnDate": "2025-06-22",
      "actualReturnDate": null,
      "companion": "李晓明",
      "relationship": "儿子",
      "companionPhone": "13812345678",
      "approvalStatus": "已通过"*/
    Long id;
    String customerName;
    String outingReason;
    String outingDate;
    String returnDate;
    String actualReturnDate;
    String companion;
    String relationship;
    String companionPhone;
    String approvalStatus;
}
