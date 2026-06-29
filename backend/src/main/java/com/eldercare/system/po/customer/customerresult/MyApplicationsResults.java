package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

import java.util.List;

@Data
public class MyApplicationsResults {
    private List<MyApplicationItem> records;
    private Long total;

    public MyApplicationsResults(List<MyApplicationItem> records, int total) {
        this.records = records;
        this.total = (long) total;
    }
}
