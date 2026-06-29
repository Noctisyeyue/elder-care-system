package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

import java.util.List;

@Data
public class CustomerListResult {
    private List<CustomerItem> records;
    private Long total;
}
