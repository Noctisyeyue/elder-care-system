package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

import java.util.List;

@Data
public class CustomerOutingListResult {
    private List<CustomerOutingItem> records;
    private Long total;
}
