package com.eldercare.system.vo.room;

import lombok.Data;

import java.util.List;

/**
 * 房间结果视图
 */
@Data
public class RoomResultVO {

    private String label; // 楼层或分组显示文本
    private List<RoomOptionVO> options; // 房间选项列表

    /**
     * 创建房间结果视图
     *
     * @param label 楼层或分组显示文本
     * @param options 房间选项列表
     */
    public RoomResultVO(String label, List<RoomOptionVO> options) {
        this.label = label;
        this.options = options;
    }
}
