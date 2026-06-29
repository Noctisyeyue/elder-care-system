package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 外出记录
 */
@TableName("outing_record")
@Data
public class OutingRecord extends BaseEntity {

    /** 外出记录ID */
    @TableId(value = "outing_record_id", type = IdType.AUTO)
    private Long outingRecordId;

    /** 客户ID */
    private Long customerId;

    /** 护工的用户ID */
    private Long userId;

    /** 外出原因 */
    private String reason;

    /** 外出时间 */
    private String outingDate;

    /** 预计返回时间 */
    private String expectedReturnDate;

    /** 陪同人员 */
    private String accompany;

    /** 陪同人员与老人关系 */
    private String relationship;

    /** 陪同人员电话 */
    private String accompanyTel;

    /** 实际返回时间 */
    private String realReturnDate;

    /** 状态：'0'通过 '1'不通过 '2'已提交 '3'未提交 */
    private String status;

    /** 审批人ID */
    private Long examingBy;

    /** 审批时间 */
    private String examingDate;
}
