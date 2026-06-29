package com.eldercare.system.dto.diet;

import lombok.Data;

@Data
public class CustomerListRequest {
    /*
    date: string, // 日期，格式：YYYY-MM-DD
    pork: string, // 有无猪肉，0-1，0代表无猪肉，1代表有猪肉
    customerName: string, // 客户姓名，用于模糊搜索，值为空搜索全部
    pageNum: number, // 当前页码，从1开始
    pageSize: number // 每页显示条数
    */
    private String date;
    private Integer pork;
    private String customerName;
    private Integer pageNum;
    private Integer pageSize;
}
