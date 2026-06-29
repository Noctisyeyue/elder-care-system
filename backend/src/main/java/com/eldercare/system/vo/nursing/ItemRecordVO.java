package com.eldercare.system.vo.nursing;

import lombok.Data;

/**
 * 护理项目执行记录视图
 */
@Data
public class ItemRecordVO {
    /*
    id: number;        // 护理项目记录ID
    code: string;      // 项目编号
    name: string;      // 项目名称
    price: number;     // 项目价格
    frequency: string; // 执行周次，例如："每周一次"
    count: number;     // 执行次数
    buyDate: string;   // 购买日期，格式：YYYY-MM-DD
    buyCount: number;  // 购买数量
    expireDate: string;// 到期日期，格式：YYYY-MM-DD
    */
    private Long id;
    private String code;
    private String name;
    private Float price;
    private String frequency;
    private Integer count;
    private String buyDate;
    private Integer buyCount;
    private String expireDate;
}
