package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

@Data
public class CustomerItem {
    private Long id;
    private String customerName;
    private Integer age;
    private String gender;
    private String nation;
    private String dateOfBirth;
    private String idNumber;
    private String bloodType;
    private String familyMember;
    private String tel;
    private String familyMemberTel;
    private String buildingNumber;
    private String roomNumber;
    private String bedNumber;
    private String checkInDate;
    private String contractEndDate;
    private String physicalMentalStatus;
    private String nursingLevel;
    private String levelId;
    private String caregiver;
    private String customerType;
}