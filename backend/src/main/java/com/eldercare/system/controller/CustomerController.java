package com.eldercare.system.controller;

import com.eldercare.system.annotation.OperationLog;
import com.eldercare.system.dto.caregiver.PurchasedItemsRequest;
import com.eldercare.system.vo.caregiver.PurchasedItemsListVO;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.customer.*;
import com.eldercare.system.vo.customer.*;
import com.eldercare.system.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 客户管理控制器 */
@Tag(name = "客户管理接口")
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Operation(summary = "获取客户列表")
    @GetMapping("/list")
    public ApiResult<CustomerListVO> list(CustomerListRequest request) {
        return customerService.list(request);
    }

    @OperationLog(module = "客户管理", operation = "登记客户", actionType = "ADD")
    @Operation(summary = "注册新客户")
    @PostMapping("/register")
    public ApiResult register(@RequestBody CustomerRegisterRequest param) {
        return customerService.register(param);
    }

    @OperationLog(module = "客户管理", operation = "修改客户信息", actionType = "UPDATE", targetId = "#id")
    @Operation(summary = "更新客户信息")
    @PutMapping("/update/{id}")
    public ApiResult update(@PathVariable Long id, @RequestBody CustomerRegisterRequest param) {
        return customerService.update(id, param);
    }

    @OperationLog(module = "客户管理", operation = "删除客户", actionType = "DELETE", targetId = "#id")
    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public ApiResult delete(@PathVariable Long id) {
        return customerService.delete(id);
    }

    @Operation(summary = "删除客户床位")
    @DeleteMapping("/{id}/bed/{bedNumber}")
    public ApiResult deleteBed(@PathVariable Long id, @PathVariable Long bedNumber) {
        return customerService.deleteBed(id, bedNumber);
    }

    @Operation(summary = "获取退住客户列表")
    @GetMapping("/checkout/list")
    public ApiResult<CustomerCheckOutListVO> checkoutList(CustomerListRequest  request) {
        return customerService.checkoutList(request);
    }

    @OperationLog(module = "客户管理", operation = "审批退住申请", actionType = "APPROVE", targetId = "#id")
    @Operation(summary = "审批客户退住申请")
    @PostMapping("/checkout/approve/{id}")
    public ApiResult approveCheckout(@PathVariable Long id, @RequestBody Map<String,Object> params,@RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.approveCheckout(id, params, token);
    }

    @Operation(summary = "获取外出客户列表")
    @GetMapping("/outing/list")
    public ApiResult<CustomerOutingListVO> outingList(CustomerListRequest  request) {
        return customerService.outingList(request);
    }

    @OperationLog(module = "客户管理", operation = "审批外出申请", actionType = "APPROVE", targetId = "#id")
    @Operation(summary = "审批客户外出申请")
    @PostMapping("/outing/approve/{id}")
    public ApiResult approveOuting(@PathVariable Long id, @RequestBody Map<String,Object> params,@RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.approveOuting(id, params, token);
    }

    @Operation(summary = "获取未分配健康管家的客户列表")
    @GetMapping("/noCaregiver")
    public ApiResult<CustomerNoCaregiverListVO> noCaregiver(CustomerListRequest request) {
        return customerService.listnoCaregiver(request);
    }

    @Operation(summary = "查询客户已购买护理项目列表")
    @GetMapping("/purchasedItems")
    public ApiResult<PurchasedItemsListVO> purchasedItems(PurchasedItemsRequest request) {
        return customerService.purchasedItems(request);
    }

    @Operation(summary = "检查当前客户是否已有该护理项目")
    @GetMapping("/isPurchased")
    public ApiResult isPurchased(Long customerId, Long itemId) {
        return customerService.isPurchased(customerId, itemId);
    }

    @OperationLog(module = "客户管理", operation = "购买护理项目", actionType = "ADD")
    @Operation(summary = "为客户购买护理项目")
    @PostMapping("/buyItem")
    public ApiResult<Map<String, Boolean>> buyItems(@RequestBody List<BuyItemRequest> requests) {
        return customerService.buyItems(requests);
    }

    @Operation(summary = "按健康管家筛选客户")
    @GetMapping("/myCustomers")
    public ApiResult<CustomerNoCaregiverListVO> myCustomers(CustomerListRequest request,@RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.listMyCustomers(request,token);
    }

    @OperationLog(module = "客户管理", operation = "申请外出", actionType = "APPLY", targetId = "#param.customerId")
    @Operation(summary = "健康管家为客户提出外出申请请求")
    @PostMapping("/outing/apply")
    public ApiResult applyOuting(@RequestBody OutingRequest param, @RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.outingApply(param,token);
    }

    @Operation(summary = "健康管家查询自己服务的客户外出申请列表")
    @GetMapping("/outing/myApplications")
    public ApiResult<MyApplicationListVO> myApplications(CustomerListRequest request, @RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.listmyApplications(request,token);
    }

    @Operation(summary = "健康管家查询自己服务的客户退住申请列表")
    @GetMapping("/checkout/myApplications")
    public ApiResult<MyCheckoutApplicationListVO> myCheckoutApplications(CustomerListRequest request, @RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.listmyCheckoutApplications(request,token);
    }

    @OperationLog(module = "客户管理", operation = "登记回院时间", actionType = "UPDATE", targetId = "#id")
    @Operation(summary = "当客户实际返回后，登记实际的回院时间")
    @PutMapping("/outing/return/{id}")
    public ApiResult returnOuting(@PathVariable Long id, @RequestBody Map<String, String>  params) {
        return customerService.returnOuting(id, params.get("actualReturnDate"));
    }

    @OperationLog(module = "客户管理", operation = "撤销外出申请", actionType = "CANCEL", targetId = "#id")
    @Operation(summary = "撤销自己的外出申请")
    @PostMapping("/outing/cancel/{id}")
    public ApiResult cancelOuting(@PathVariable Long id) {
        return customerService.cancelOuting(id);
    }

    @OperationLog(module = "客户管理", operation = "申请退住", actionType = "APPLY", targetId = "#param.customerId")
    @Operation(summary = "健康管家为客户提出退住申请请求")
    @PostMapping("/checkout/apply")
    public ApiResult checkoutApply(@RequestBody CheckoutRequest param,@RequestHeader(value = "Authorization", required = false) String token) {
        return customerService.checkApply(param,token);
    }

    @OperationLog(module = "客户管理", operation = "撤销退住申请", actionType = "CANCEL", targetId = "#id")
    @Operation(summary = "撤销自己的退住申请")
    @PostMapping("/checkout/cancel/{id}")
    public ApiResult cancelCheckout(@PathVariable Long id) {
        return customerService.cancelCheckout(id);
    }

    @Operation(summary = "获取客户总数")
    @GetMapping("/count")
    public ApiResult<Long> count() {
        return customerService.count();
    }

    @Operation(summary = "获取指定月份的客户数量")
    @GetMapping("/monthCount")
    public ApiResult<Long> monthCount(String date) {
        return customerService.monthCount(date);
    }

    @Operation(summary = "获取指定年份的客户数量统计")
    @GetMapping("/yearCount")
    public ApiResult<List<Long>> yearCount(String year) {
        return customerService.yearCount(year);
    }

    @Operation(summary = "获取客户护理级别分布")
    @GetMapping("/nursingLevelDistribution")
    public ApiResult<List<NursingLevelDistributionVO>> nursingLevelDistribution() {
        return customerService.nursingLevelDistribution();
    }
}
