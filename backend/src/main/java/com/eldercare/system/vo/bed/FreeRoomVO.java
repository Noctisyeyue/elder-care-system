package com.eldercare.system.vo.bed;

import lombok.Data;

import java.util.List;

/**
 * 空闲房间选项视图
 */
@Data
public class FreeRoomVO {

    private String label; // 房间显示名称
    private List<PairVO> options; // 床位选项列表
}
