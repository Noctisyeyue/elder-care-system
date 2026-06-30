package com.eldercare.system.dto.bed;

import lombok.Data;

/**
 * 床位调换请求
 */
@Data
public class BedSwapRequest {

    private Long oldBedId; // 原床位记录 ID
    private String oldBedEndDate; // 原床位结束日期
    private String newBedDetails; // 新床位详情，格式为房间号#床位号
    private String newBedStartDate; // 新床位开始日期
    private String newBedEndDate; // 新床位结束日期
}
