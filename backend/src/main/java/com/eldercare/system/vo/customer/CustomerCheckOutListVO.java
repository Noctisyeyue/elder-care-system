package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户退住申请列表视图
 */
@Data
public class CustomerCheckOutListVO {
    private List<CustomerCheckOutVO> records;
    private Long total;
}
