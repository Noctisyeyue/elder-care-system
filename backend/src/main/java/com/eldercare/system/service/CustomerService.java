package com.eldercare.system.service;

import com.eldercare.system.dto.caregiver.PurchasedItemsRequest;
import com.eldercare.system.vo.caregiver.PurchasedItemsListVO;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.customer.*;
import com.eldercare.system.vo.customer.*;

import java.util.List;
import java.util.Map;

/** 客户服务接口 */
public interface CustomerService {

    /**
     * 分页查询客户列表
     *
     * @param request 查询参数
     * @return 客户列表
     */
    ApiResult<CustomerListVO> list(CustomerListRequest request);

    /**
     * 登记客户入住信息
     *
     * @param param 客户登记参数
     * @return 操作结果
     */
    ApiResult register(CustomerRegisterRequest param);

    /**
     * 修改客户信息
     *
     * @param id    客户ID
     * @param param 客户修改参数
     * @return 操作结果
     */
    ApiResult update(Long id, CustomerRegisterRequest param);

    /**
     * 删除客户
     *
     * @param id 客户ID
     * @return 操作结果
     */
    ApiResult delete(Long id);

    /**
     * 解除客户床位绑定
     *
     * @param id        客户ID
     * @param bedNumber 床位号
     * @return 操作结果
     */
    ApiResult deleteBed(Long id, Long bedNumber);

    /**
     * 查询退住申请列表
     *
     * @param request 查询参数
     * @return 退住申请列表
     */
    ApiResult<CustomerCheckOutListVO> checkoutList(CustomerListRequest request);

    /**
     * 审批退住申请
     *
     * @param id     退住申请ID
     * @param params 审批参数
     * @param token  登录令牌
     * @return 操作结果
     */
    ApiResult approveCheckout(Long id, Map<String, Object> params,String token);

    /**
     * 管理员直接办理自理老人退住
     *
     * @param param 退住办理参数
     * @param token 登录令牌
     * @return 操作结果
     */
    ApiResult directCheckout(CheckoutRequest param, String token);

    /**
     * 查询外出申请列表
     *
     * @param request 查询参数
     * @return 外出申请列表
     */
    ApiResult<CustomerOutingListVO> outingList(CustomerListRequest request);

    /**
     * 审批外出申请
     *
     * @param id     外出申请ID
     * @param params 审批参数
     * @param token  登录令牌
     * @return 操作结果
     */
    ApiResult approveOuting(Long id, Map<String, Object> params, String token);

    /**
     * 查询未分配健康管家的客户
     *
     * @param request 查询参数
     * @return 客户列表
     */
    ApiResult<CustomerNoCaregiverListVO> listnoCaregiver(CustomerListRequest request);

    /**
     * 查询客户已购护理项目
     *
     * @param request 查询参数
     * @return 已购项目列表
     */
    ApiResult<PurchasedItemsListVO> purchasedItems(PurchasedItemsRequest request);

    /**
     * 判断客户是否已购买指定护理项目
     *
     * @param customerId 客户ID
     * @param itemId     护理项目ID
     * @return 购买状态
     */
    ApiResult isPurchased(Long customerId, Long itemId);

    /**
     * 批量购买护理项目
     *
     * @param requests 购买参数列表
     * @return 每条购买结果
     */
    ApiResult<Map<String, Boolean>> buyItems(List<BuyItemRequest> requests);

    /**
     * 查询当前健康管家的服务对象
     *
     * @param request 查询参数
     * @param token   登录令牌
     * @return 客户列表
     */
    ApiResult<CustomerNoCaregiverListVO> listMyCustomers(CustomerListRequest request, String token);

    /**
     * 提交外出申请
     *
     * @param param 外出申请参数
     * @param token 登录令牌
     * @return 操作结果
     */
    ApiResult outingApply(OutingRequest param, String token);

    /**
     * 查询当前健康管家的外出申请
     *
     * @param request 查询参数
     * @param token   登录令牌
     * @return 外出申请列表
     */
    ApiResult<MyApplicationListVO> listmyApplications(CustomerListRequest request, String token);

    /**
     * 登记客户外出返回
     *
     * @param id               外出申请ID
     * @param actualReturnDate 实际返回日期
     * @return 操作结果
     */
    ApiResult returnOuting(Long id, String actualReturnDate);

    /**
     * 取消外出申请
     *
     * @param id 外出申请ID
     * @return 操作结果
     */
    ApiResult cancelOuting(Long id);

    /**
     * 提交退住申请
     *
     * @param param 退住申请参数
     * @param token 登录令牌
     * @return 操作结果
     */
    ApiResult checkApply(CheckoutRequest param, String token);

    /**
     * 取消退住申请
     *
     * @param id 退住申请ID
     * @return 操作结果
     */
    ApiResult cancelCheckout(Long id);

    /**
     * 查询当前健康管家的退住申请
     *
     * @param request 查询参数
     * @param token   登录令牌
     * @return 退住申请列表
     */
    ApiResult<MyCheckoutApplicationListVO> listmyCheckoutApplications(CustomerListRequest request, String token);

    /**
     * 统计客户总数
     *
     * @return 客户总数
     */
    ApiResult<Long> count();

    /**
     * 统计指定月份新增客户数量
     *
     * @param date 月份字符串
     * @return 新增客户数量
     */
    ApiResult<Long> monthCount(String date);

    /**
     * 统计指定年份每月新增客户数量
     *
     * @param year 年份字符串
     * @return 每月新增客户数量列表
     */
    ApiResult<List<Long>> yearCount(String year);

    /**
     * 获取客户护理级别分布统计
     *
     * @return 护理级别分布列表
     */
    ApiResult<List<NursingLevelDistributionVO>> nursingLevelDistribution();
}
