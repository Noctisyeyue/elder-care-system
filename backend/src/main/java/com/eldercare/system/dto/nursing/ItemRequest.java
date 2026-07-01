package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理项目请求参数
 */
@Data
public class ItemRequest {

    private Long id; // 护理项目 ID
    private String code; // 护理项目编号
    private String name; // 护理项目名称
    private String frequency; // 执行周期
    private Integer count; // 执行次数
    private String desc; // 护理项目描述
    private String status; // 护理项目状态
}
