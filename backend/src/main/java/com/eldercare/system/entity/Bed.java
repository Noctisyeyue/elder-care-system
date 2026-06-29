package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 床位信息
 */
@TableName("bed")
@Data
@EqualsAndHashCode(callSuper = false)
public class Bed extends BaseEntity {

    /** 床位ID */
    @TableId(value = "bed_id", type = IdType.AUTO)
    private Long bedId;

    /** 床位号 */
    private String bedNo;

    /** 状态（free空闲 used有人 out外出） */
    private String status;

    /** 房间ID */
    private Long roomId;
}
