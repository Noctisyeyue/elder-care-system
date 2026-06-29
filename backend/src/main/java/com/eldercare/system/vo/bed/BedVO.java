package com.eldercare.system.vo.bed;

import lombok.Data;

@Data
/**
 * BedVO 视图
 */
public class BedVO {
    /*
    "id": 1,           // 床位记录的唯一标识符
    "customerName": "毛兰英",
    "gender": "女",
    "age": 65,
    "bedDetails": "606#2001-1", // 床位详情，包含楼号(默认606)、房间号、床位号
    "usageStartDate": "2023-11-30", // 使用起始日期 (格式: YYYY-MM-DD)
    "usageEndDate": "2025-06-20", // 使用结束日期 (格式: YYYY-MM-DD)
    "history": 1        // 历史记录标识符 (1: 正在使用, 0: 使用历史)
    "status": 'used'        // 状态'free' | 'used' | 'out'(空闲，有人，外出)
    */
    private long id;
    private String customerName;
    private String gender;
    private String age;
    private String bedDetails;
    private String usageStartDate;
    private String usageEndDate;
    private String history;
    private String status;
}
