package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 护理项目记录
 */
@TableName("nursing_item_record")
@Data
public class NursingItemRecord extends BaseEntity {

    /** 护理项目记录ID */
    @TableId(value = "nursing_item_record_id", type = IdType.AUTO)
    private Long nursingItemRecordId;

    /** 客户ID */
    private Long customerId;

    /** 护理项目ID */
    private Long nursingItemId;

    /** 护理项目编号 */
    private String nursingItemCode;

    /** 护理项目名称 */
    private String nursingItemName;

    /** 价格 */
    private Float price;

    /** 执行周期 */
    private String executionCycle;

    /** 总执行次数 */
    private Integer executionTimes;

    /** 已执行次数 */
    private Integer executedTimes;

    /** 购买日期 */
    private String purchasingDate;

    /** 购买次数 */
    private Integer purchasingTimes;

    /** 服务到期日期 */
    private String expirationDate;

    /** 状态：0启用 1停用 */
    private String status;

    /** 描述 */
    private String description;
}
