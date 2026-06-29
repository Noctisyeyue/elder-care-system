package com.eldercare.system.po.nursing.result;

import lombok.Data;

import java.util.List;

@Data
public class NursingRecordListResult {
    private List<RecordResult> list;
    private Integer total;
}
