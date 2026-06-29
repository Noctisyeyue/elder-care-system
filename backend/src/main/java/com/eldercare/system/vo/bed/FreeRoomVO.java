package com.eldercare.system.vo.bed;

import lombok.Data;

import java.util.List;

@Data
/**
 * FreeRoomVO 视图
 */
public class FreeRoomVO {
    private String label;
    private List<PairVO> options;
}
