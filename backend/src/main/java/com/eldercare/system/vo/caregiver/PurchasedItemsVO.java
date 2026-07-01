package com.eldercare.system.vo.caregiver;

import lombok.Data;

/**
 * 已购买护理项目视图
 */
@Data
public class PurchasedItemsVO {

    private Long id; // 护理项目 ID
    private String customerName; // 客户姓名
    private String code; // 护理项目编号
    private String name; // 护理项目名称
    private int executionTimes; // 单份执行次数
    private int remain; // 剩余次数
    private String expireDate; // 到期时间
    private String status; // 护理项目状态

    /**
     * 创建已购买护理项目视图
     *
     * @param id 护理项目 ID
     * @param customerName 客户姓名
     * @param code 护理项目编号
     * @param name 护理项目名称
     * @param executionTimes 单份执行次数
     * @param remain 剩余次数
     * @param expireDate 到期时间
     * @param status 护理项目状态
     */
    public PurchasedItemsVO(Long id, String customerName, String code, String name,
                            int executionTimes, int remain, String expireDate, String status) {
        this.id = id;
        this.customerName = customerName;
        this.code = code;
        this.name = name;
        this.executionTimes = executionTimes;
        this.remain = remain;
        this.expireDate = expireDate;
        this.status = status;
    }
}
