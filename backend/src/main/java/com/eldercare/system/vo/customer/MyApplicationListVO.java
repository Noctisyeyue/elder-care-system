package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 我的外出申请列表视图
 */
@Data
public class MyApplicationListVO {

    private List<MyApplicationVO> records; // 外出申请记录列表
    private Long total; // 总记录数

    /**
     * 创建我的外出申请列表视图
     *
     * @param records 外出申请记录列表
     * @param total 总记录数
     */
    public MyApplicationListVO(List<MyApplicationVO> records, int total) {
        this.records = records;
        this.total = (long) total;
    }
}
