package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

import java.util.List;

@Data
public class CustomerCheckOutListResult {
    private List<CustomerCheckOutItem> records;
    private Long total;
}
