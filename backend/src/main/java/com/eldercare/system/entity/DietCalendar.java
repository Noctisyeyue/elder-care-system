package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 膳食日历实体
 */
@TableName("diet_calendar")
@Data
public class DietCalendar extends BaseEntity {
    /** 膳食日历ID */
    @TableId(value = "diet_calendar_id", type = IdType.AUTO)
    private Long dietCalendarId;
    /** 日期 */
    private String date;
}
