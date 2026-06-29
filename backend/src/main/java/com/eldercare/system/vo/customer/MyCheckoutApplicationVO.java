package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 我的退住申请视图
 */
@Data
public class MyCheckoutApplicationVO {
    /* id: number,                    // 申请ID
      customerName: string,          // 客户姓名
      checkOutType: string,          // 退住类型（如："正常退住"）
      checkOutReason: string,        // 退住原因
      checkOutDate: string,          // 退住时间（格式：YYYY-MM-DD）
      approvalStatus: string         // 审批状态（"已提交"、"通过"、"不通过"、"已撤销"）*/
    Long id;
    String customerName;
    String checkOutType;
    String checkOutReason;
    String checkOutDate;
    String approvalStatus;
}
