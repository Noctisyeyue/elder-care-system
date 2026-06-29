package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class LevelItemListParams {
    private Long levelId;
    private Integer pageNum;
    private Integer pageSize;
}
