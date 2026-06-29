package com.eldercare.system.vo.bed;

import lombok.Data;

import java.util.List;

@Data
public class BedListVO {
    private List<BedVO> list;
    private Integer total;
}
