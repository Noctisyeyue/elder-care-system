package com.eldercare.system.po.diet.params;

import lombok.Data;

import java.util.List;

@Data
public class SaveCustomerSetMealParams {
    private Long setMealId;
    private List<Long> customerIds;
    private String date;
}
