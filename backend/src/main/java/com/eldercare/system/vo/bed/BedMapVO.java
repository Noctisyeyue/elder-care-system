package com.eldercare.system.vo.bed;

import com.eldercare.system.entity.Bed;
import lombok.Data;

import java.util.List;

@Data
/**
 * BedMapVO 视图
 */
public class BedMapVO {
    private String roomNumber;
    private List<Bed> beds;
    private List<BedVO> details;
}
