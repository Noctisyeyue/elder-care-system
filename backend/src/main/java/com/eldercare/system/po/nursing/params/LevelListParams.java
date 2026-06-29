package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class LevelListParams {
    private String status;
    private Integer pageNum;
    private Integer pageSize;
}
