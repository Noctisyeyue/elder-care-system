package com.eldercare.system.dto.diet;

import lombok.Data;

@Data
public class DietCalendarMonthListRequest {
    /*
    year : number  年份
    month: number   月份
    */
    private Integer year;
    private Integer month;
}
