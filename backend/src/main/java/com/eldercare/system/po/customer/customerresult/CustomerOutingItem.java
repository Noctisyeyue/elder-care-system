package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

@Data
public class CustomerOutingItem {
    private Long id;
    private String customerName;
    private String outingDate;
    private String returnDate;
    private String outingReason;
    private String approvalTime;
    private String actualReturnDate;//实际返回时间
    private String approvalPerson;
    private String approvalStatus;
    private String bedNumber;
}
