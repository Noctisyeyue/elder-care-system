package com.eldercare.system.dto.bed;

import lombok.Data;

/**
 * 床位调换请求
 */
@Data
public class BedSwapRequest {

    private Long oldBedId; // 原床位记录 ID
    private String oldBedEndDate; // 原床位结束日期
    private String newBuildingNumber; // 新楼号
    private String newRoomNumber; // 新房间号
    private String newBedNumber; // 新床号
    private String newBedStartDate; // 新床位开始日期
    private String newBedEndDate; // 新床位结束日期
}
