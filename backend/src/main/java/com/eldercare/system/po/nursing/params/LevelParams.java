package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class LevelParams {
    /*
    id: number,      // 护理级别ID
    level:	 string,    // 护理级别名称
    status: string   // '启用' 或 '停用'
    */
    private Long id;
    private String level;
    private String status;
}
