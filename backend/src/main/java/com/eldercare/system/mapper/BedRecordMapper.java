package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.BedRecord;

/** BedRecordMapper */
public interface BedRecordMapper extends BaseMapper<BedRecord> {
    int deleteBedRecord(Long id, Long bedNumber);

    int updateCheckOutRecord(Long id, Long userId, String status, String date, String type);

    void updateBedRecordUsageEndDate(Long id, String date, String number);

    int updateOutingRecord(Long id, Long userId, String status, String date);

    Long selectBedRecordIdByBedIdAndHistory(Long oldBedId, String number);

    BedRecord selectByBedId(Long oldBedId);
}
