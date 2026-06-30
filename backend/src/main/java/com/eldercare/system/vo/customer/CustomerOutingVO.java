package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 客户外出申请视图
 */
@Data
public class CustomerOutingVO {

    private Long id; // 外出申请 ID
    private String customerName; // 客户姓名
    private String outingDate; // 外出日期
    private String returnDate; // 预计返回日期
    private String outingReason; // 外出原因
    private String approvalTime; // 审批时间
    private String actualReturnDate; // 实际返回时间
    private String approvalPerson; // 审批人
    private String approvalStatus; // 审批状态
    private String bedNumber; // 床位号
}
