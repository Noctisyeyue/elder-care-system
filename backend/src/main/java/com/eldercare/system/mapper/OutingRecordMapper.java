package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.OutingRecord;
import com.eldercare.system.po.customer.customerresult.MyApplicationItem;

import java.util.List;

/** OutingRecordMapper */
public interface OutingRecordMapper extends BaseMapper<OutingRecord> {
    void returnOuting(Long id, String actualReturnDate);

    List<MyApplicationItem> selectMyApplications(Long userId, int pageStart, Integer pageSize, String customerName);

    int countMyApplications(Long userId, String customerName);

    void cancelOuting(Long id);

    void updateOutingRecordDelFlag(Long id);

    void updateOutingRecordDelFlagByUserId(Long aLong);
}
