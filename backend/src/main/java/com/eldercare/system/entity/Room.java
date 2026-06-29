package com.eldercare.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 房间信息
 */
@TableName("room")
@Data
@EqualsAndHashCode(callSuper = false)
public class Room extends BaseEntity {

    /** 房间ID */
    @TableId(value = "room_id")
    private Long roomId;

    /** 房间号 */
    private String roomNo;

    /** 楼号 */
    private String building;

    /** 楼层 */
    private String floor;
}
