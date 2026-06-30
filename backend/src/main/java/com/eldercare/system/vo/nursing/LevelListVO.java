package com.eldercare.system.vo.nursing;

import lombok.Data;

import java.util.List;

/**
 * 护理级别列表视图
 */
@Data
public class LevelListVO {

    private List<LevelVO> records; // 护理级别记录列表
    private Long total; // 总记录数
}
