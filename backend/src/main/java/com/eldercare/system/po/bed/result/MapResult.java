package com.eldercare.system.po.bed.result;

import com.eldercare.system.entity.Bed;
import lombok.Data;

import java.util.List;

@Data
public class MapResult {
    private String roomNumber;
    private List<Bed> beds;
    private List<BedResult> details;
}
