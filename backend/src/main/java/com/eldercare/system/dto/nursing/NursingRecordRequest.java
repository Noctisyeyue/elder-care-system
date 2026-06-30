package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理记录请求参数
 */
@Data
public class NursingRecordRequest {

    private Long itemId; // 护理项目 ID
    private Long customerId; // 客户 ID
    private String name; // 护理项目名称
    private String code; // 护理项目编号
    private Double price; // 护理项目价格
    private String nursingTime; // 护理时间
    private Integer times; // 护理次数
}
