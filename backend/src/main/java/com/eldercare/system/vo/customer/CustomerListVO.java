package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户列表视图
 */
@Data
public class CustomerListVO {

    private List<CustomerVO> records; // 客户记录列表
    private Long total; // 总记录数
}
