package com.eldercare.system.vo.nursing;

import lombok.Data;

/**
 * 护理级别视图
 */
@Data
public class LevelVO {

    private Long id; // 护理级别 ID
    private String level; // 护理级别名称
    private String status; // 护理级别状态
}
