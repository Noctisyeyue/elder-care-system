package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class NursingRecordListParams {
    /*
    customerId: number    // 客户ID
    pageNum: number
    pageSize: number
    */
    private Long customerId;
    private Integer pageNum;
    private Integer pageSize;
}
