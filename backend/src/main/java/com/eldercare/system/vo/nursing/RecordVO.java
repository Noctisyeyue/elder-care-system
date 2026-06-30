package com.eldercare.system.vo.nursing;

import lombok.Data;

/**
 * 护理执行记录视图
 */
@Data
public class RecordVO {

    private Long id; // 护理记录 ID
    private String code; // 护理项目编号
    private String name; // 护理项目名称
    private Integer times; // 护理次数
    private String nursingStaff; // 护理人员
    private String staffPhone; // 护理人员手机号
    private String nursingTime; // 护理时间
}
