package com.eldercare.system.service;

import com.eldercare.system.po.caregiver.params.PurchasedItemsRequest;
import com.eldercare.system.po.caregiver.results.PurchasedItemsListResult;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.customer.customerparams.*;
import com.eldercare.system.po.customer.customerresult.*;

import java.util.List;
import java.util.Map;

/** 客户服务接口 */
public interface CustomerService {
    ApiResult<CustomerListResult> list(CustomerListRequest request);

    ApiResult register(CustomerRegisterParam param);

    ApiResult update(Long id, CustomerRegisterParam param);

    ApiResult delete(Long id);

    ApiResult deleteBed(Long id, Long bedNumber);

    ApiResult<CustomerCheckOutListResult> checkoutList(CustomerListRequest request);

    ApiResult approveCheckout(Long id, Map<String, Object> params,String token);

    ApiResult<CustomerOutingListResult> outingList(CustomerListRequest request);

    ApiResult approveOuting(Long id, Map<String, Object> params, String token);

    ApiResult<CustomerNoCaregiverListResult> listnoCaregiver(CustomerListRequest request);

    ApiResult<PurchasedItemsListResult> purchasedItems(PurchasedItemsRequest request);

    ApiResult isPurchased(Long customerId, Long itemId);

    ApiResult<Map<String, Boolean>> buyItems(List<BuyItemRequest> requests);

    ApiResult<CustomerNoCaregiverListResult> listMyCustomers(CustomerListRequest request, String token);

    ApiResult outingApply(OutingParam param, String token);

    ApiResult<MyApplicationsResults> listmyApplications(CustomerListRequest request, String token);

    ApiResult returnOuting(Long id, String actualReturnDate);

    ApiResult cancelOuting(Long id);

    ApiResult checkApply(CheckoutParam param, String token);

    ApiResult cancelCheckout(Long id);

    ApiResult<MyCheckoutApplicationsResults> listmyCheckoutApplications(CustomerListRequest request, String token);

    ApiResult<Long> count();

    ApiResult<Long> monthCount(String date);

    ApiResult<List<Long>> yearCount(String year);
}
