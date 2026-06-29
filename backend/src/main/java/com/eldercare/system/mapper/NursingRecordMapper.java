package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.NursingRecord;

import java.util.List;

/** NursingRecordMapper */
public interface NursingRecordMapper extends BaseMapper<NursingRecord> {
    List<NursingRecord> selectByCustomerId(Long customerId, Integer pageStart, Integer pageSize);

    Integer countByCustomerId(Long customerId);

    void removeById(Long recordId);

    void updateNursingRecordDelFlag(Long id);

    void updateNursingRecordDelFlagByUserId(Long aLong);

    Long countMonthCareCustomer(Long userId, String date);
}
