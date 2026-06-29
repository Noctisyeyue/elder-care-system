package com.eldercare.system.service;

import com.eldercare.system.po.caregiver.params.CustomersByCareIdRequest;
import com.eldercare.system.po.caregiver.results.CaregiverListResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.caregiver.results.HomeResult;
import com.eldercare.system.vo.customer.CustomerNoCaregiverListVO;
import com.eldercare.system.po.user.UserList;

/** 健康管家服务接口 */
public interface CaregiverService {

    /**
     * 分页查询健康管家列表
     *
     * @param user 查询参数
     * @return 健康管家列表
     */
    ApiResult<CaregiverListResult> list(UserList user);

    /**
     * 查询健康管家服务对象列表
     *
     * @param request 查询参数
     * @return 客户列表
     */
    ApiResult<CustomerNoCaregiverListVO> listCustomers(CustomersByCareIdRequest request);

    /**
     * 为健康管家添加服务对象
     *
     * @param caregiverId 健康管家用户ID
     * @param customerId  客户ID
     * @return 操作结果
     */
    ApiResult addCustomer(Long caregiverId, Long customerId);

    /**
     * 移除健康管家的服务对象
     *
     * @param caregiverId 健康管家用户ID
     * @param customerId  客户ID
     * @return 操作结果
     */
    ApiResult removeCustomer(Long caregiverId, Long customerId);

    /**
     * 移除健康管家的全部服务对象
     *
     * @param caregiverId 健康管家用户ID
     * @return 操作结果
     */
    ApiResult removeAllCustomers(Long caregiverId);

    /**
     * 查询健康管家首页统计数据
     *
     * @param token 登录令牌
     * @return 首页统计数据
     */
    ApiResult<HomeResult> homeStats(String token);
}
