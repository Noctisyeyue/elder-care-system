package com.eldercare.system.po.caregiver.params;

import lombok.Data;

@Data
public class CustomersByCareIdRequest {
    private Long caregiverId;      // 可为空
    private Integer pageNum;         // 必填
    private Integer pageSize;
    private String customerName;
}
