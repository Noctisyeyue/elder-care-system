package com.eldercare.system.po.bed.result;

import lombok.Data;

import java.util.List;

@Data
public class ListResult {
    private List<BedResult> list;
    private Integer total;
}
