package com.eldercare.system.dto.customer;

import lombok.Data;

/**
 * 客户入住登记请求
 */
@Data
public class CustomerRegisterRequest {
    /*参数：
{
  customerName: string,    // 必填，客户姓名
  gender: string,         // 必填，性别：'男' 或 '女'
  dateOfBirth: string,    // 必填，出生日期，格式：YYYY-MM-DD
  idNumber: string,       // 必填，身份证号
  bloodType: string,      // 必填，血型：'A'/'B'/'O'/'AB'
  familyMember: string,   // 必填，家属姓名
  contactPhone: string,   // 必填，联系电话
  buildingNumber: string, // 必填，楼号
  roomNumber: string,     // 必填，房间号
  bedNumber: string,      // 必填，床位号
  checkInDate: string,    // 必填，入住日期，格式：YYYY-MM-DD
  contractEndDate: string,// 必填，合同到期日期，格式：YYYY-MM-DD
  physicalMentalStatus: string  // 必填，身心状况
}*/
    private String customerName;
    private String gender;
    private String dateOfBirth;
    private String idNumber;
    private String bloodType;
    private String familyMember;
    private String tel;
    private String familyMemberTel;
    private String nation;
    private String buildingNumber;
    private String roomNumber;
    private String bedNumber;
    private String checkInDate;
    private String contractEndDate;
    private String physicalMentalStatus;
    private String customerType;

}
