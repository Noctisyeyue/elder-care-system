package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 护理项目
 */
@TableName("nursing_item")
@Data
public class NursingItem extends BaseEntity {

    /** 护理项目ID */
    @TableId(value = "nursing_item_id", type = IdType.AUTO)
    private Long nursingItemId;

    /** 护理项目编号 */
    private String code;

    /** 护理项目名称 */
    private String nursingItemName;

    /** 执行周期 */
    private String executionCycle;

    /** 执行次数 */
    private Integer executionTimes;

    /** 状态：0启用 1停用 */
    private String status;

    /** 描述 */
    private String description;
}
