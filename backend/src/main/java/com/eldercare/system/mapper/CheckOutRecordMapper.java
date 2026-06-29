package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.CheckOutRecord;
import com.eldercare.system.po.customer.customerresult.MyCheckoutApplicationItem;

import java.util.List;

/** CheckOutRecordMapper */
public interface CheckOutRecordMapper extends BaseMapper<CheckOutRecord> {
    void cancelCheckout(Long id);

    List<MyCheckoutApplicationItem> selectMyCheckoutApplications(Long userId, int pageStart, Integer pageSize, String customerName);

    int countMyCheckoutApplications(Long userId, String customerName);

    void updateCheckoutRecordDelFlag(Long id);

    void updateCheckoutRecordDelFlagByUserId(Long aLong);
}
