package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户退住申请列表视图
 */
@Data
public class CustomerCheckOutListVO {

    private List<CustomerCheckOutVO> records; // 退住申请记录列表
    private Long total; // 总记录数
}
