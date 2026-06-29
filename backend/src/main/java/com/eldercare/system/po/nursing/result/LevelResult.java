package com.eldercare.system.po.nursing.result;

import lombok.Data;

@Data
public class LevelResult {
    /*
    id: 1,
    evel: "一级",
    status: "启用"
    */
    private Long id;
    private String level;
    private String status;
}
