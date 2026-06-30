package com.eldercare.system.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户外出申请列表视图
 */
@Data
public class CustomerOutingListVO {

    private List<CustomerOutingVO> records; // 外出申请记录列表
    private Long total; // 总记录数
}
