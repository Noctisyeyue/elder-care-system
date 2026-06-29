package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户外出申请列表视图
 */
@Data
public class CustomerOutingListVO {
    private List<CustomerOutingVO> records;
    private Long total;
}
