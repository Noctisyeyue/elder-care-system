package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 我的退住申请视图
 */
@Data
public class MyCheckoutApplicationVO {

    Long id; // 退住申请 ID
    String customerName; // 客户姓名
    String checkOutType; // 退住类型
    String checkOutReason; // 退住原因
    String checkOutDate; // 退住日期
    String approvalStatus; // 审批状态
}
