package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理级别请求参数
 */
@Data
public class LevelRequest {
    /*
    id: number,      // 护理级别ID
    level:	 string,    // 护理级别名称
    status: string   // '启用' 或 '停用'
    */
    private Long id;
    private String level;
    private String status;
}
