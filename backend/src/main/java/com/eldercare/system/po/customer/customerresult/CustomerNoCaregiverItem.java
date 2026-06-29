package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

@Data
public class CustomerNoCaregiverItem {
    private Long id;
    private String customerName;
    private Integer age;
    private String gender;
    private String tel;
    private String buildingNumber;
    private String roomNumber;
    private String bedNumber;
    private String nursingLevel;
    private String customerType;
}
