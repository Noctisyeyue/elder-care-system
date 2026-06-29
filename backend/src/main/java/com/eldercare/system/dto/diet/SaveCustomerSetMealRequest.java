package com.eldercare.system.dto.diet;

import lombok.Data;

import java.util.List;

@Data
public class SaveCustomerSetMealRequest {
    private Long setMealId;
    private List<Long> customerIds;
    private String date;
}
