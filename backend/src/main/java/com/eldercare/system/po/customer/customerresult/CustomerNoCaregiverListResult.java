package com.eldercare.system.po.customer.customerresult;

import lombok.Data;

import java.util.List;

@Data
public class CustomerNoCaregiverListResult {
    private List<CustomerNoCaregiverItem> list;
    private Long total;
}
