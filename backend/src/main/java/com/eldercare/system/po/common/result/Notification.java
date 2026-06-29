package com.eldercare.system.po.common.result;

import lombok.Data;

@Data
public class Notification {
    private Long checkOutApplyCount;
    private Long outingApplyCount;
    private Boolean dietConfigured;
}
