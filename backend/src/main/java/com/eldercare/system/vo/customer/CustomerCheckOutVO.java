package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 客户退住申请视图
 */
@Data
public class CustomerCheckOutVO {

    private Long id; // 退住申请 ID
    private String customerName; // 客户姓名
    private String checkInDate; // 入住日期
    private String checkOutDate; // 退住日期
    private String checkOutType; // 退住类型
    private String checkOutReason; // 退住原因
    private String approvalTime; // 审批时间
    private String approvalPerson; // 审批人
    private String approvalStatus; // 审批状态
    private String buildingNumber; // 楼号
    private String roomNumber; // 房间号
    private String bedNumber; // 床号
}
