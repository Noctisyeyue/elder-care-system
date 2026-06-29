package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 我的退住申请列表视图
 */
@Data
public class MyCheckoutApplicationListVO {
    private List<MyCheckoutApplicationVO> records;
    private Long total;

    public MyCheckoutApplicationListVO(List<MyCheckoutApplicationVO> records, int total) {
        this.records = records;
        this.total = (long) total;
    }
}
