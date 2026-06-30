package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 未分配健康管家的客户列表视图
 */
@Data
public class CustomerNoCaregiverListVO {

    private List<CustomerNoCaregiverVO> list; // 未分配健康管家的客户列表
    private Long total; // 总记录数
}
