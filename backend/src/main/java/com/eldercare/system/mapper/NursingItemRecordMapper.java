package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.NursingItemRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** NursingItemRecordMapper */
public interface NursingItemRecordMapper extends BaseMapper<NursingItemRecord> {
    List<NursingItemRecord> selectByCustomerId(Long customerId);

    void removeByCustomerId(Long customerId);

    void removeById(Long id);

    void updateTimes(Long customerId, String name, Integer times);

    int updateTimesByRecordId(@Param("id") Long id, @Param("times") Integer times);
}
