package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 我的外出申请视图
 */
@Data
public class MyApplicationVO {

    Long id; // 外出申请 ID
    String customerName; // 客户姓名
    String outingReason; // 外出原因
    String outingDate; // 外出日期
    String returnDate; // 预计返回日期
    String actualReturnDate; // 实际返回日期
    String companion; // 陪同人
    String relationship; // 陪同人与客户关系
    String companionPhone; // 陪同人电话
    String approvalStatus; // 审批状态
}
