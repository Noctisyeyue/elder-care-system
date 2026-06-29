package com.eldercare.system.po.diet.result;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDishListResult {
    /*
    customerId: number,      // 客户ID
    customerName: string,    // 客户姓名
    age: number,     // 年龄
    gender: string,  // 性别：'male'/'female'
    nation: string,  // 民族
    bedNumber: string, // 床号
    */
    private Long customerId;
    private String customerName;
    private Integer age;
    private String gender;
    private String nation;
    private String bedNumber;
    private List<DishResult> breakfast;
    private List<DishResult> lunch;
    private List<DishResult> dinner;
}
