package com.eldercare.system.dto.diet;

import lombok.Data;

@Data
public class DishRequest {
    /*
    dishId: number	// 膳食ID
    name: string;    // 膳食名称
    category: string;  // 品类（素菜/荤菜/主食/饮品）
    pork: string;   // 是否含猪肉（0含有猪肉/1不含猪肉）
    time: string;  // 时间（0早餐/1午餐/2晚餐）
    status: string;   // 状态（on/off）
    */
    private Long dishId;
    private String name;
    private String category;
    private String pork;
    private String time;
    private String status;
}
