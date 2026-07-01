package com.eldercare.system.vo.bed;

import lombok.Data;

/**
 * 床位状态分布视图
 */
@Data
public class BedStatusDistributionVO {

    private Long free;  // 空闲床位数量
    private Long used;  // 占用床位数量
    private Long out;   // 外出床位数量
    private Long total; // 床位总数
}
