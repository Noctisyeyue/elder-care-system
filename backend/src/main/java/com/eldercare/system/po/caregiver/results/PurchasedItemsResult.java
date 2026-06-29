package com.eldercare.system.po.caregiver.results;

import lombok.Data;

@Data
public class PurchasedItemsResult {
    /*id: 1,     // 项目ID
    customerName: "李福星", // 客户姓名
    code: "N001", // 项目编号
    name: "基础护理", // 项目名称
    price: 100,  // 价格
    executionTimes,  // （一份）执行次数
    remain: 10,  // 剩余数量
    expireDate: "2024-12-31", // 到期时间
    status: "normal" // 状态：normal-正常, expired-到期, arrears-欠费
    */

    private Long id;
    private String customerName;
    private String code;
    private String name;
    private float price;
    private int executionTimes;
    private int remain;
    private String expireDate;
    private String status;
    public PurchasedItemsResult(Long id, String customerName, String code, String name, float price, int executionTimes, int remain, String expireDate, String status){
        this.id = id;
        this.customerName = customerName;
        this.code = code;
        this.name = name;
        this.price = price;
        this.executionTimes = executionTimes;
        this.remain = remain;
        this.expireDate = expireDate;
        this.status = status;
    }
}
