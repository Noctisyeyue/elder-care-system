package com.eldercare.system.po.nursing.result;

import lombok.Data;

@Data
public class RecordResult {
    /*
    id: number,           // 记录ID
    code: string,         // 护理项目编号
    name: string,         // 护理项目名称
    times: number,        // 护理数量
    nursingStaff: string, // 护理人员
    staffPhone: string,   // 护理人员手机
    nursingTime: string   // 护理时间（格式：YYYY-MM-DD）
    */
    private Long id;
    private String code;
    private String name;
    private Integer times;
    private String nursingStaff;
    private String staffPhone;
    private String nursingTime;
}
