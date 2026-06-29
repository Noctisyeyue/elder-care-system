package com.eldercare.system.vo.room;

import lombok.Data;

import java.util.List;

/**
 * 房间结果视图
 */
@Data
public class RoomResultVO {
    /** 标签 */
    private String label;
    /** 选项列表 */
    private List<RoomOptionVO> options;

    public RoomResultVO(String label, List<RoomOptionVO> options) {
        this.label = label;
        this.options = options;
    }
}
