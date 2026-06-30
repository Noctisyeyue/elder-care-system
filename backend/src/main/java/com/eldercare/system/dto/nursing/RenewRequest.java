package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理项目续费请求
 */
@Data
public class RenewRequest {

    private Long customerId; // 客户 ID
    private Long itemId; // 护理项目 ID
    private Integer purchasingTimes; // 购买次数
    private String expireDate; // 到期时间
}
