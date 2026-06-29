package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 未分配健康管家的客户视图
 */
@Data
public class CustomerNoCaregiverVO {
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
