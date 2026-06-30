package com.eldercare.system.dto.nursing;

import lombok.Data;

/**
 * 新增护理项目执行记录请求
 */
@Data
public class ItemRecordAddRequest {

    private Long itemId; // 护理项目 ID
    private String buyDate; // 购买日期
    private Integer buyCount; // 购买数量
    private String expireDate; // 到期日期
}
