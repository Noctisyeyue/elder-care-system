package com.eldercare.system.vo.nursing;

import lombok.Data;

/**
 * 护理项目执行记录视图
 */
@Data
public class ItemRecordVO {

    private Long id; // 护理项目记录 ID
    private String code; // 护理项目编号
    private String name; // 护理项目名称
    private Float price; // 护理项目价格
    private String frequency; // 执行周期
    private Integer count; // 执行次数
    private String buyDate; // 购买日期
    private Integer buyCount; // 购买数量
    private String expireDate; // 到期日期
}
