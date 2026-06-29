package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户列表视图
 */
@Data
public class CustomerListVO {
    private List<CustomerVO> records;
    private Long total;
}
