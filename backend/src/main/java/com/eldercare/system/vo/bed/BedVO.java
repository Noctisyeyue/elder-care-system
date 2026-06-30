package com.eldercare.system.vo.bed;

import lombok.Data;

/**
 * 床位信息视图
 */
@Data
public class BedVO {

    private long id; // 床位记录 ID
    private String customerName; // 客户姓名
    private String gender; // 性别
    private String age; // 年龄
    private String buildingNumber; // 楼号
    private String roomNumber; // 房间号
    private String bedNumber; // 床号
    private String usageStartDate; // 使用开始日期
    private String usageEndDate; // 使用结束日期
    private String history; // 使用记录状态
    private String status; // 床位状态
}
