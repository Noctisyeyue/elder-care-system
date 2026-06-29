package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

@Data
public class CustomerCheckOutItem {
    private Long id;
    private String customerName;
    private String checkInDate;
    private String checkOutDate;
    private String checkOutType;
    private String checkOutReason;
    private String approvalTime;
    private String approvalPerson;
    private String approvalStatus;
    private String bedNumber;

}
