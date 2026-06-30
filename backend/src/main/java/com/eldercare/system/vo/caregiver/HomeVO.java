package com.eldercare.system.vo.caregiver;

import lombok.Data;

import java.util.List;

/**
 * 健康管家首页视图
 */
@Data
public class HomeVO {

    private String userName; // 用户名
    private Long dailyCareCount; // 今日护理次数
    private Long compareCareCount; // 对比护理次数
    private Long totalCareCount; // 累计护理次数
    private Long completedCareCount; // 已完成护理次数
    private Long uncompletedCareCount; // 未完成护理次数
    private Long totalCaredPeople; // 累计服务人数
    private List<Long> counts; // 趋势统计数据
    private ApplicationStatusVO outingApplicationStatus; // 外出申请状态统计
    private ApplicationStatusVO checkoutApplicationStatus; // 退住申请状态统计
}
