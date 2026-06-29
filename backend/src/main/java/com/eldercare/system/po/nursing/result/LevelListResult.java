package com.eldercare.system.po.nursing.result;

import lombok.Data;

import java.util.List;

@Data
public class LevelListResult {
    private List<LevelResult> records;
    private Long total;
}
