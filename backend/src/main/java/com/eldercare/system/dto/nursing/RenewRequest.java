package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 护理项目续费请求
 */
@Data
public class RenewRequest {
    /* customerId: number,  // 客户ID
    itemId: number,    // 护理项目ID
    purchasingTimes: number,    // 购买数量
    expireDate: string  // 到期时间，格式：YYYY-MM-DD
    */
    private Long customerId;
    private Long itemId;
    private Integer purchasingTimes;
    private String expireDate;
}
