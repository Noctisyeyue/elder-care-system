package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 退住记录实体
 */
@TableName("check_out_record")
@Data
public class CheckOutRecord extends BaseEntity {
    /** 退住记录ID */
    @TableId(type = IdType.AUTO, value = "check_out_record_id")
    private Long checkOutRecordId;
    /** 客户ID */
    private Long customerId;
    /** 用户ID */
    private Long userId;
    /** 退住日期 */
    private String checkOutDate;
    /** 类型 */
    private String type;
    /** 原因 */
    private String reason;
    /** 状态 */
    private String status;
    /** 审核人 */
    private String examingBy;
    /** 审核日期 */
    private String examingDate;
}
