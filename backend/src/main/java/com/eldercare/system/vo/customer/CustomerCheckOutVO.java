package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 客户退住申请视图
 */
@Data
public class CustomerCheckOutVO {
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
