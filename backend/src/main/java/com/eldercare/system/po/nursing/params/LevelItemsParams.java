package com.eldercare.system.po.nursing.params;

import lombok.Data;

import java.util.List;

@Data
public class LevelItemsParams {
    /*
    levelId: 1,      // 当前护理级别ID
    itemId: [2,3]     // 护理项目ID数组
    */
    private Long levelId;
    private List<Long> itemIds;
}
