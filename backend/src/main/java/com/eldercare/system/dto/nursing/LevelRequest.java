package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别请求参数
 */
@Data
public class LevelRequest {

    private Long id; // 护理级别 ID
    private String level; // 护理级别名称
    private String status; // 护理级别状态
}
