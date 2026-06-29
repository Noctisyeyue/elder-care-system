package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 护理记录
 */
@TableName("nursing_record")
@Data
public class NursingRecord extends BaseEntity {

    /** 护理记录ID */
    @TableId(value = "nursing_record_id", type = IdType.AUTO)
    private Long nursingRecordId;

    /** 客户ID */
    private Long customerId;

    /** 用户ID（护工ID） */
    private Long userId;

    /** 护理项目ID */
    private Long nursingItemId;

    /** 护理项目编号 */
    private String nursingItemCode;

    /** 护理项目名称 */
    private String nursingItemName;

    /** 护理次数 */
    private Integer nursingTimes;

    /** 护理时间 */
    private String nursingDate;

    /** 描述 */
    private String description;
}
