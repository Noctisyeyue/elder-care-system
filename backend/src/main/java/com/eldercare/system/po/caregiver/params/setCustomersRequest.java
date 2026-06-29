package com.eldercare.system.po.caregiver.params;

import lombok.Data;

import java.util.List;

@Data
public class setCustomersRequest {
    Long caregiverId;
    List<Long> customerIds;
}
