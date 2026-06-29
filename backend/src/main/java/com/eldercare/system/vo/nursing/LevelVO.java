package com.eldercare.system.vo.nursing;

import lombok.Data;

/**
 * 护理级别视图
 */
@Data
public class LevelVO {
    /*
    id: 1,
    evel: "一级",
    status: "启用"
    */
    private Long id;
    private String level;
    private String status;
}
