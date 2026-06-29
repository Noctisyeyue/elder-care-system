package com.eldercare.system.dto.caregiver;

import lombok.Data;

@Data
/** CustomersByCareId请求 */
public class CustomersByCareIdRequest {
    private Long caregiverId;      // 可为空
    private Integer pageNum;         // 必填
    private Integer pageSize;
    private String customerName;
}
