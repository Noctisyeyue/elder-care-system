package com.eldercare.system.vo.nursing;

import lombok.Data;

import java.util.List;

/**
 * 护理项目列表视图
 */
@Data
public class ItemListVO {

    private List<ItemVO> records; // 护理项目记录列表
    private Long total; // 总记录数
}
