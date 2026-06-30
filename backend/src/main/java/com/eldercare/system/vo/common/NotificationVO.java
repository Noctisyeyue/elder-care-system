package com.eldercare.system.vo.common;

import lombok.Data;

/**
 * 通知视图
 */
@Data
public class NotificationVO {

    private Long checkOutApplyCount; // 退住申请数
    private Long outingApplyCount; // 外出申请数
    private Boolean dietConfigured; // 膳食是否已配置
}
