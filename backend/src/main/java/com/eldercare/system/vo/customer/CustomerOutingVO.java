package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 客户外出申请视图
 */
@Data
public class CustomerOutingVO {
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
