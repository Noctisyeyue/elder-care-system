package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 我的退住申请列表视图
 */
@Data
public class MyCheckoutApplicationListVO {

    private List<MyCheckoutApplicationVO> records; // 退住申请记录列表
    private Long total; // 总记录数

    /**
     * 创建我的退住申请列表视图
     *
     * @param records 退住申请记录列表
     * @param total 总记录数
     */
    public MyCheckoutApplicationListVO(List<MyCheckoutApplicationVO> records, int total) {
        this.records = records;
        this.total = (long) total;
    }
}
