package com.eldercare.system.vo.common;

import lombok.Data;

/**
 * 通知视图
 */
@Data
public class NotificationVO {
    /** 退住申请数 */
    private Long checkOutApplyCount;
    /** 外出申请数 */
    private Long outingApplyCount;
    /** 膳食是否已配置 */
    private Boolean dietConfigured;
}
