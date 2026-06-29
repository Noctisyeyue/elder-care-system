package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 我的（外出）申请列表视图
 */
@Data
public class MyApplicationListVO {
    private List<MyApplicationVO> records;
    private Long total;

    public MyApplicationListVO(List<MyApplicationVO> records, int total) {
        this.records = records;
        this.total = (long) total;
    }
}
