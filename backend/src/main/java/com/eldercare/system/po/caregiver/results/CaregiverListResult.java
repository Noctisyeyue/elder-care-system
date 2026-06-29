package com.eldercare.system.po.caregiver.results;

import lombok.Data;

import java.util.List;

@Data
public class CaregiverListResult {
    private List<CaregiverResult> list;
    private int total;
    public CaregiverListResult(List<CaregiverResult> list, int total) {
        this.list = list;
        this.total = total;
    }
}
