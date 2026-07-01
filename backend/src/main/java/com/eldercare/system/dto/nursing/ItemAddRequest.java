package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 新增护理项目请求
 */
@Data
public class ItemAddRequest {

    private String code; // 护理项目编号
    private String name; // 护理项目名称
    private String frequency; // 执行周期
    private Integer count; // 执行次数
    private String desc; // 护理项目描述
    private String status; // 护理项目状态
}
