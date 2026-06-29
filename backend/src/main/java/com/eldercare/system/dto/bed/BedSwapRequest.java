package com.eldercare.system.dto.bed;

import lombok.Data;

@Data
/**
 * BedSwapRequest 请求
 */
public class BedSwapRequest {
    /*
    oldBedId: number;        // 旧床位记录的ID
    oldBedEndDate: string;   // 旧床位结束日期，格式：YYYY-MM-DD
    newBedDetails: string;   // 新床位详情，格式：606#房间号-床位号
    newBedStartDate: string; // 新床位开始日期，格式：YYYY-MM-DD
    newBedEndDate: string;   // 新床位结束日期，格式：YYYY-MM-DD
    */
    private Long oldBedId;
    private String oldBedEndDate;
    private String newBedDetails;
    private String newBedStartDate;
    private String newBedEndDate;
}
