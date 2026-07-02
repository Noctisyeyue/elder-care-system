package com.eldercare.system.vo.customer;

import lombok.Data;

/**
 * 客户护理级别分布视图
 */
@Data
public class NursingLevelDistributionVO {

    private String name;   // 护理级别名称
    private Long value;    // 该级别的客户数量
    private Long levelId;  // 护理级别ID
}
