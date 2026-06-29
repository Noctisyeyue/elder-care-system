package com.eldercare.system.service;

import com.eldercare.system.po.caregiver.params.CustomersByCareIdRequest;
import com.eldercare.system.po.caregiver.results.CaregiverListResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.caregiver.results.HomeResult;
import com.eldercare.system.po.customer.customerresult.CustomerNoCaregiverListResult;
import com.eldercare.system.po.user.UserList;

/** 健康管家服务接口 */
public interface CaregiverService {
    ApiResult<CaregiverListResult> list(UserList user);

    ApiResult<CustomerNoCaregiverListResult> listCustomers(CustomersByCareIdRequest request);

    ApiResult addCustomer(Long caregiverId, Long customerId);

    ApiResult removeCustomer(Long caregiverId, Long customerId);

    ApiResult removeAllCustomers(Long caregiverId);

    ApiResult<HomeResult> homeStats(String token);
}
