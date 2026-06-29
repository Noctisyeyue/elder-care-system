package com.eldercare.system.po.nursing.params;

import lombok.Data;

@Data
public class ItemParams {
    /*
    id: number,        // 项目ID
    code: string,      // 项目编号
    name: string,      // 项目名称
    price: number,     // 价格
    frquency: string,    // 执行周期
    count: number,     // 执行次数
    desc: string,      // 描述
    status: string     // 状态：'启用' 或 '停用'
    */
    private Long id;
    private String code;
    private String name;
    private Float price;
    private String frequency;
    private Integer count;
    private String desc;
    private String status;
}
