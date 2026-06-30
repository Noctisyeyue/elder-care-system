package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 未分配健康管家的客户视图
 */
@Data
public class CustomerNoCaregiverVO {

    private Long id; // 客户 ID
    private String customerName; // 客户姓名
    private Integer age; // 年龄
    private String gender; // 性别
    private String tel; // 联系电话
    private String buildingNumber; // 楼号
    private String roomNumber; // 房间号
    private String bedNumber; // 床位号
    private String nursingLevel; // 护理级别
    private String customerType; // 客户类型
}
