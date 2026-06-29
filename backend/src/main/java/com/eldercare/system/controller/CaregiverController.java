package com.eldercare.system.controller;

import com.eldercare.system.dto.caregiver.CustomersByCareIdRequest;
import com.eldercare.system.dto.caregiver.SetCustomersRequest;
import com.eldercare.system.vo.caregiver.CaregiverListVO;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.caregiver.HomeVO;
import com.eldercare.system.vo.customer.CustomerNoCaregiverListVO;
import com.eldercare.system.dto.user.UserListRequest;
import com.eldercare.system.service.CaregiverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 健康管家控制器 */
@Tag(name = "健康管家接口")
@RestController
@RequestMapping("/caregiver")
public class CaregiverController {

    @Autowired
    private CaregiverService caregiverService;

    // 查询健康管家列表
    @Operation(summary = "查询健康管家列表")
    @GetMapping("/list")
    public ApiResult<CaregiverListVO> list(UserListRequest user) {
        return caregiverService.list(user);
    }

    // 根据管家的ID查询客户列表
    @Operation(summary = "根据管家的ID查询客户列表")
    @GetMapping("/customers")
    public ApiResult<CustomerNoCaregiverListVO> noCaregiver(CustomersByCareIdRequest request) {
        return caregiverService.listCustomers(request);
    }

    // 添加客户到服务管家客户信息列表（分配服务管家）
    @Operation(summary = "添加客户到服务管家客户信息列表")
    @PostMapping("/addCustomer")
    public ApiResult addCustomer(@RequestBody Map<String, Long> body) {
        Long caregiverId = body.get("caregiverId");
        Long customerId = body.get("customerId");
        return caregiverService.addCustomer(caregiverId, customerId);
    }

    // 批量给健康管家设置客户
    @Operation(summary = "批量给健康管家设置客户")
    @PostMapping("/setCustomers")
    public ApiResult setCustomers(@RequestBody SetCustomersRequest body) {
        Long caregiverId = body.getCaregiverId();
        ApiResult result = new ApiResult();
        result.setCode(200);
        result.setMessage("添加成功");
        if (caregiverService.removeAllCustomers(caregiverId).getCode() != 200) {
            result.setCode(500);
            result.setMessage("删除失败");
        }
        for (Long customerId : body.getCustomerIds()) {
            if (caregiverService.addCustomer(caregiverId, customerId).getCode() != 200) {
                result.setCode(500);
                result.setMessage("添加失败");
            }
        }
        return result;
    }

    // 删除客户
    @Operation(summary = "删除客户")
    @DeleteMapping("/removeCustomer")
    public ApiResult removeCustomer(@RequestBody Map<String, Long> body) {
        Long caregiverId = body.get("caregiverId");
        Long customerId = body.get("customerId");
        return caregiverService.removeCustomer(caregiverId, customerId);
    }

    // 获取首页统计信息
    @Operation(summary = "获取首页统计信息")
    @GetMapping("/homeStats")
    public ApiResult<HomeVO> homeStats(@RequestHeader(value = "Authorization", required = false) String token) {
        return caregiverService.homeStats(token);
    }
}
