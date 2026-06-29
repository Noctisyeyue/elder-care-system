package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

import java.util.List;

@Data
public class MyCheckoutApplicationsResults {
    private List<MyCheckoutApplicationItem> records;
    private Long total;

    public MyCheckoutApplicationsResults(List<MyCheckoutApplicationItem> records, int total) {
        this.records = records;
        this.total = (long) total;
    }
}
