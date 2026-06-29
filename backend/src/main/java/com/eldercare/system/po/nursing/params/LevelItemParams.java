package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class LevelItemParams {
    /*
    levelId: 1,      // 当前护理级别ID
    itemId: 2     // 护理项目ID
    */
    private Long levelId;
    private Long itemId;
}
