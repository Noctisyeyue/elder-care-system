package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class RenewParams {
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
