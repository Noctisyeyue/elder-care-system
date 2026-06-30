package com.eldercare.system.vo.caregiver;

import lombok.Data;

/**
 * 申请状态统计视图
 */
@Data
public class ApplicationStatusVO {

    private Long approved; // 已通过数量
    private Long rejected; // 已拒绝数量
    private Long submitted; // 已提交数量
    private Long cancelled; // 已撤销数量
}
