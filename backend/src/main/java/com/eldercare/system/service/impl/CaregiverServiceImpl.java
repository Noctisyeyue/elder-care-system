package com.eldercare.system.service.impl;
import com.eldercare.system.service.CaregiverService;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.entity.*;
import com.eldercare.system.mapper.*;
import com.eldercare.system.dto.caregiver.CustomersByCareIdRequest;
import com.eldercare.system.vo.caregiver.ApplicationStatusVO;
import com.eldercare.system.vo.caregiver.CaregiverListVO;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.caregiver.HomeVO;
import com.eldercare.system.vo.customer.CustomerNoCaregiverVO;
import com.eldercare.system.vo.customer.CustomerNoCaregiverListVO;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.dto.user.UserListRequest;
import com.eldercare.system.vo.caregiver.CaregiverVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 健康管家服务实现
 */
@Service
public class CaregiverServiceImpl implements CaregiverService {

    /** 用户 Mapper */
    @Autowired
    private UserMapper userMapper;

    /** 客户 Mapper */
    @Autowired
    private CustomerMapper customerMapper;

    /** 护理记录 Mapper */
    @Autowired
    private NursingRecordMapper nursingRecordMapper;

    /** 外出记录 Mapper */
    @Autowired
    private OutingRecordMapper outingRecordMapper;

    /** 退住记录 Mapper */
    @Autowired
    private CheckOutRecordMapper checkOutRecordMapper;

    /**
     * 分页查询健康管家列表
     *
     * @param request 查询参数
     * @return 健康管家列表
     */
    @Override
    public ApiResult<CaregiverListVO> list(UserListRequest request) {
        ApiResult<CaregiverListVO> result = new ApiResult<>();

        // 1. 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (request.getUserName() != null && !request.getUserName().isEmpty()) {
            queryWrapper.like("real_name", request.getUserName());
        }

        // 2. 分页查询
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();
        queryWrapper.last("LIMIT " + pageStart + ", " + request.getPageSize());
        queryWrapper.eq("role_id", 2);
        List<User> caregivers = userMapper.selectList(queryWrapper);

        // 3. 数据转换
        List<CaregiverVO> caregiverResults = new ArrayList<>();
        for (User caregiver : caregivers) {
            CaregiverVO item = new CaregiverVO();
            item.setId(caregiver.getUserId());
            item.setName(caregiver.getRealName());
            item.setPhone(caregiver.getPhone());
            item.setEmail(caregiver.getEmail());
            item.setGender(caregiver.getGender());
            caregiverResults.add(item);
        }

        // 4. 查询总数
        // 创建一个新的 QueryWrapper 用于统计，避免包含 LIMIT
        QueryWrapper<User> countQueryWrapper = new QueryWrapper<>();
        if (request.getUserName() != null && !request.getUserName().isEmpty()) {
            countQueryWrapper.like("real_name", request.getUserName());
        }
        countQueryWrapper.eq("role_id", 2);

        // 使用不带 LIMIT 的 wrapper 查询总数
        int total = Math.toIntExact(userMapper.selectCount(countQueryWrapper));

        // 5. 组装返回值
        CaregiverListVO response = new CaregiverListVO(caregiverResults, total);
        result.setData(response);
        if (!caregiverResults.isEmpty()) {
            result.setCode(200);
            result.setMessage("查询成功");
        } else {
            result.setCode(200);
            result.setMessage("数据为空");
        }

        return result;
    }

    /**
     * 查询健康管家服务对象列表
     *
     * @param request 查询参数
     * @return 客户列表
     */
    @Override
    public ApiResult<CustomerNoCaregiverListVO> listCustomers(CustomersByCareIdRequest request) {
        ApiResult<CustomerNoCaregiverListVO> result = new ApiResult<>();
        int pageStart = (request.getPageNum() - 1) * request.getPageSize();
        List<CustomerNoCaregiverVO> items = userMapper.listcustomerItems(
                request.getCaregiverId(),
                pageStart,
                request.getPageSize(),
                request.getCustomerName()
        );
        CustomerNoCaregiverListVO response = new CustomerNoCaregiverListVO();
        if(items.isEmpty()){
            result.setCode(200);
            response.setList(items);
            response.setTotal(0L);
            result.setData(response);
            result.setMessage("数据为空");
            return result;
        }
        long total = userMapper.countCustomerItems(
                request.getCaregiverId(),
                request.getCustomerName()
        );

        response.setList(items);
        response.setTotal(total);
        result.setData(response);
        if(!items.isEmpty()){
            result.setCode(200);
            result.setMessage("查询成功");
        }else {
            result.setCode(200);
            result.setMessage("数据为空");
        }

        return result;
    }

    /**
     * 为健康管家添加服务对象
     *
     * @param caregiverId 健康管家用户ID
     * @param customerId  客户ID
     * @return 操作结果
     */
    @Override
    public ApiResult addCustomer(Long caregiverId, Long customerId) {
        ApiResult result = new ApiResult();
        if (caregiverId == null || customerId == null) {
            result.setCode(500);
            result.setMessage("参数错误");
            return result;
        }
        try {
            if (userMapper.addCustomer(caregiverId, customerId) == 1) {
                result.setCode(200);
                result.setMessage("添加成功");
            }else {
                result.setCode(500);
                result.setMessage("添加失败");
            }
        }catch (Exception  e){
            result.setCode(500);
            result.setMessage("添加失败，其他错误");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 移除健康管家的服务对象
     *
     * @param caregiverId 健康管家用户ID
     * @param customerId  客户ID
     * @return 操作结果
     */
    @Override
    public ApiResult removeCustomer(Long caregiverId, Long customerId) {
        ApiResult result = new ApiResult();
        if (caregiverId == null || customerId == null) {
            result.setCode(500);
            result.setMessage("参数错误");
            return result;
        }
        try {
            if (userMapper.removeCustomer(caregiverId, customerId) > 0) {
                result.setCode(200);
                result.setMessage("删除成功");
            }else {
                result.setCode(500);
                result.setMessage("删除失败");
            }
        }catch (Exception  e){
            result.setCode(500);
            result.setMessage("删除失败，其他错误");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 移除健康管家的全部服务对象
     *
     * @param caregiverId 健康管家用户ID
     * @return 操作结果
     */
    @Override
    public ApiResult removeAllCustomers(Long caregiverId) {
        //查询客户表所有user_id=caregiverId的客户，将其user_id设为null
        ApiResult result = new ApiResult();
        try{
            //
            customerMapper.removeAllCustomers(caregiverId.intValue());
            result.setCode(200);
            result.setMessage("删除成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("删除失败，其他错误");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询健康管家首页统计数据
     *
     * @param token 登录令牌
     * @return 首页统计数据
     */
    @Override
    public ApiResult<HomeVO> homeStats(String token) {
        // 获取健康管理员首页数据
        // 变量准备
        ApiResult<HomeVO> result = new ApiResult<>();
        HomeVO data = new HomeVO();
        data.setCounts(new ArrayList<>());
        ApplicationStatusVO outingApplicationstatus = new ApplicationStatusVO();
        ApplicationStatusVO checkOutApplicationstatus = new ApplicationStatusVO();
        Long userId;
        Long dailyCarecount;
        Long yesterdayCarecount;
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
         // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期
        String formattedDate = currentDate.format(formatter);
        // 根据Token获取用户名
        String username;
        try {
            if (token != null && token.startsWith("Bearer ")){
                token = token.substring(7);
            }
            Map<String, Claim> claims = JWTUtil.getPayloadFromToken(token);
            Claim usernameClaim = claims.get("userName");
            if (usernameClaim == null) {
                // 处理 username 不存在的情况
                result.setCode(401);
                result.setMessage("用户名不存在");
                return result;
            }
            username = usernameClaim.asString();
            // 继续业务逻辑
        } catch (Exception e) {
            result.setCode(401);
            result.setMessage("Token解析失败");
            return result;
        }
        data.setUserName(username);
        // 数据库查询
        // 获取用户ID
        try {
            userId = userMapper.selectIdByUsername(username);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("用户ID获取失败");
            return result;
        }
        // 获取今日护理次数
        QueryWrapper<NursingRecord> todayNursingCountQueryWrapper = new QueryWrapper<>();
        todayNursingCountQueryWrapper.eq("user_id", userId);
        todayNursingCountQueryWrapper.eq("nursing_date", formattedDate);
        todayNursingCountQueryWrapper.eq("del_flag", "0");
        try {
            dailyCarecount = nursingRecordMapper.selectCount(todayNursingCountQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取今日护理次数数据库错误");
            return result;
        }
        data.setDailyCareCount(dailyCarecount);
        // 获取昨日护理次数
        QueryWrapper<NursingRecord> yesterdayNursingCountQueryWrapper = new QueryWrapper<>();
        yesterdayNursingCountQueryWrapper.eq("user_id", userId);
        yesterdayNursingCountQueryWrapper.eq("nursing_date", currentDate.minusDays(1).format(formatter));
        yesterdayNursingCountQueryWrapper.eq("del_flag", "0");
        try {
            yesterdayCarecount = nursingRecordMapper.selectCount(yesterdayNursingCountQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取昨日护理次数数据库错误");
            return result;
        }
        // 计算较昨日提升数量
        Long compareCareCount = dailyCarecount - yesterdayCarecount;
        data.setCompareCareCount(compareCareCount);
        // 获取总护理次数
        try {
            data.setTotalCareCount(userMapper.getTotalCareCount(userId) == null ? 0 : userMapper.getTotalCareCount(userId));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取总护理次数数据库错误");
            return result;
        }
        // 获取已完成护理次数
        QueryWrapper<NursingRecord> completedNursingCountQueryWrapper = new QueryWrapper<>();
        completedNursingCountQueryWrapper.eq("user_id", userId);
        completedNursingCountQueryWrapper.eq("del_flag", "0");
        try {
            data.setCompletedCareCount(nursingRecordMapper.selectCount(completedNursingCountQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取已完成护理次数数据库错误");
            return result;
        }
        // 获取未完成护理次数
        data.setUncompletedCareCount(data.getTotalCareCount() - data.getCompletedCareCount());
        // 获取累计护理人数
        QueryWrapper<Customer> totalCustomerCountQueryWrapper = new QueryWrapper<>();
        totalCustomerCountQueryWrapper.eq("user_id", userId);
        totalCustomerCountQueryWrapper.eq("del_flag", "0");
        try {
            data.setTotalCaredPeople(customerMapper.selectCount(totalCustomerCountQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取累计护理人数数据库错误");
            return result;
        }
        // 获取每月服务客户数
        String year = formattedDate.substring(0, 4);
        for (int i = 1; i <= 12; i++){
            String month = i < 10 ? "0" + i : String.valueOf(i);
            String date = year + "-" + month;
            try {
                data.getCounts().add(nursingRecordMapper.countMonthCareCustomer(userId, date));
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("获取每月服务客户数数据库错误");
                return result;
            }
        }
        // 获取外出申请情况
        QueryWrapper<OutingRecord> outingRecordQueryWrapper = new QueryWrapper<>();
        outingRecordQueryWrapper.eq("user_id", userId);
        outingRecordQueryWrapper.eq("status", "0");
        try {
            outingApplicationstatus.setApproved(outingRecordMapper.selectCount(outingRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取外出申请情况数据库错误");
            return result;
        }
        outingRecordQueryWrapper.clear();
        outingRecordQueryWrapper.eq("user_id", userId);
        outingRecordQueryWrapper.eq("status", "1");
        try {
            outingApplicationstatus.setRejected(outingRecordMapper.selectCount(outingRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取外出申请情况数据库错误");
            return result;
        }
        outingRecordQueryWrapper.clear();
        outingRecordQueryWrapper.eq("user_id", userId);
        outingRecordQueryWrapper.eq("status", "2");
        try {
            outingApplicationstatus.setSubmitted(outingRecordMapper.selectCount(outingRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取外出申请情况数据库错误");
            return result;
        }
        outingRecordQueryWrapper.clear();
        outingRecordQueryWrapper.eq("user_id", userId);
        outingRecordQueryWrapper.eq("status", "4");
        try {
            outingApplicationstatus.setCancelled(outingRecordMapper.selectCount(outingRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取外出申请情况数据库错误");
            return result;
        }
        data.setOutingApplicationStatus(outingApplicationstatus);
        // 获取退住申请情况
        QueryWrapper<CheckOutRecord> checkOutRecordQueryWrapper = new QueryWrapper<>();
        checkOutRecordQueryWrapper.eq("user_id", userId);
        checkOutRecordQueryWrapper.eq("status", "0");
        try {
            checkOutApplicationstatus.setApproved(checkOutRecordMapper.selectCount(checkOutRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取退住申请情况数据库错误");
            return result;
        }
        checkOutRecordQueryWrapper.clear();
        checkOutRecordQueryWrapper.eq("user_id", userId);
        checkOutRecordQueryWrapper.eq("status", "1");
        try {
            checkOutApplicationstatus.setRejected(checkOutRecordMapper.selectCount(checkOutRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取退住申请情况数据库错误");
            return result;
        }
        checkOutRecordQueryWrapper.clear();
        checkOutRecordQueryWrapper.eq("user_id", userId);
        checkOutRecordQueryWrapper.eq("status", "2");
        try {
            checkOutApplicationstatus.setSubmitted(checkOutRecordMapper.selectCount(checkOutRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取退住申请情况数据库错误");
            return result;
        }
        checkOutRecordQueryWrapper.clear();
        checkOutRecordQueryWrapper.eq("user_id", userId);
        checkOutRecordQueryWrapper.eq("status", "4");
        try {
            checkOutApplicationstatus.setCancelled(checkOutRecordMapper.selectCount(checkOutRecordQueryWrapper));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取退住申请情况数据库错误");
            return result;
        }
        data.setCheckoutApplicationStatus(checkOutApplicationstatus);
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }
}
