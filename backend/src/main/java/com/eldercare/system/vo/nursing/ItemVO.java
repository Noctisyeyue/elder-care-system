package com.eldercare.system.vo.nursing;

import lombok.Data;

/**
 * 护理项目视图
 */
@Data
public class ItemVO {
    /*
    "id": 1,
    "code": "HLXM0001",
    "name": "吸氧",
    "price": “60元/次”,
    "frequency": "每天",
    "count": 2,
    "desc": ‘描述性信息’ ,  // 描述
    "status": "启用"
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
