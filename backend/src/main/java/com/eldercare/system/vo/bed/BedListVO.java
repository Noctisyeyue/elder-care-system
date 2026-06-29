package com.eldercare.system.vo.bed;

import lombok.Data;

import java.util.List;

@Data
/**
 * BedListVO 视图
 */
public class BedListVO {
    private List<BedVO> list;
    private Integer total;
}
