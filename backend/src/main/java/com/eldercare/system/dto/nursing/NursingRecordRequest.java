package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理记录请求参数
 */
@Data
public class NursingRecordRequest {
    /*  "id": 3,                    // 护理项目ID，数字类型
  "name": "踩背",             // 护理项目名称，字符串
  "code": "#1234",            // 护理项目编号，字符串
  "price": 100,               // 护理项目价格，数字类型
  "nursingTime": "2024-06-21",// 护理时间，字符串，格式为YYYY-MM-DD
  "times": 1          // 护理数量，数字类型*/
    private Long itemId;
    private Long customerId;
    private String name;
    private String code;
    private Double price;
    private String nursingTime;
    private Integer times;
}
