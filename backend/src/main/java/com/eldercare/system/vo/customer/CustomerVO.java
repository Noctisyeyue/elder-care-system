package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 客户信息视图
 */
@Data
public class CustomerVO {

    private Long id; // 客户 ID
    private String customerName; // 客户姓名
    private Integer age; // 年龄
    private String gender; // 性别
    private String nation; // 民族
    private String dateOfBirth; // 出生日期
    private String idNumber; // 身份证号
    private String bloodType; // 血型
    private String familyMember; // 家属姓名
    private String tel; // 客户联系电话
    private String familyMemberTel; // 家属联系电话
    private String buildingNumber; // 楼号
    private String roomNumber; // 房间号
    private String bedNumber; // 床位号
    private String checkInDate; // 入住日期
    private String contractEndDate; // 合同到期日期
    private String physicalMentalStatus; // 身心状况
    private String nursingLevel; // 护理级别
    private String levelId; // 护理级别 ID
    private String caregiver; // 健康管家
    private String customerType; // 客户类型
}
