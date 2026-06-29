package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 床位记录实体
 */
@TableName("bed_record")
@Data
public class BedRecord extends BaseEntity implements Serializable {
    /** 床位记录ID */
    @TableId(value = "bed_record_id", type = IdType.AUTO)
    private Long bedRecordId;
    /** 床位ID */
    private Long bedId;
    /** 床位号 */
    private String bedNo;
    /** 客户ID */
    private Long customerId;
    /** 入住时间 */
    private String usageStartDate;
    /** 退住时间 */
    private String usageEndDate;
    /** 历史记录标识（0使用记录 1正在使用） */
    private String history;
}
