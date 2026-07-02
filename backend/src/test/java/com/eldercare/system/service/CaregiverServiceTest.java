package com.eldercare.system.service;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.dto.user.UserListRequest;
import com.eldercare.system.entity.User;
import com.eldercare.system.mapper.*;
import com.eldercare.system.service.impl.CaregiverServiceImpl;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.vo.caregiver.CaregiverListVO;
import com.eldercare.system.vo.caregiver.HomeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 健康管家业务单元测试。
 * 覆盖分页查询、服务对象管理、首页统计等核心流程。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("健康管家 Service 单元测试")
class CaregiverServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private NursingRecordMapper nursingRecordMapper;
    @Mock
    private OutingRecordMapper outingRecordMapper;
    @Mock
    private CheckOutRecordMapper checkOutRecordMapper;

    @InjectMocks
    private CaregiverServiceImpl caregiverService;

    @Test
    @DisplayName("list — 正常分页，返回管家列表")
    void list_正常分页_返回管家列表() {
        UserListRequest request = new UserListRequest();
        request.setUserName("张");
        request.setPageNum(1);
        request.setPageSize(10);

        User caregiver = new User();
        caregiver.setUserId(1L);
        caregiver.setRealName("张三");
        caregiver.setPhone("13800138000");
        caregiver.setEmail("zhang@test.com");
        caregiver.setGender("男");
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(caregiver));
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        ApiResult<CaregiverListVO> result = caregiverService.list(request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
        assertEquals("张三", result.getData().getList().get(0).getName());
    }

    @Test
    @DisplayName("list — 无匹配数据，返回空列表")
    void list_无匹配数据_返回空列表() {
        UserListRequest request = new UserListRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(List.of());
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        ApiResult<CaregiverListVO> result = caregiverService.list(request);

        assertEquals(200, result.getCode());
        assertEquals("数据为空", result.getMessage());
    }

    @Test
    @DisplayName("addCustomer — 正常添加，返回成功")
    void addCustomer_正常添加_返回成功() {
        when(userMapper.addCustomer(eq(1L), eq(100L))).thenReturn(1);

        ApiResult result = caregiverService.addCustomer(1L, 100L);

        assertEquals(200, result.getCode());
        assertEquals("添加成功", result.getMessage());
    }

    @Test
    @DisplayName("addCustomer — caregiverId为null，返回500")
    void addCustomer_null参数_返回参数错误() {
        ApiResult result = caregiverService.addCustomer(null, 100L);

        assertEquals(500, result.getCode());
        assertEquals("参数错误", result.getMessage());
    }

    @Test
    @DisplayName("addCustomer — mapper返回0，添加失败")
    void addCustomer_添加失败_返回500() {
        when(userMapper.addCustomer(eq(1L), eq(100L))).thenReturn(0);

        ApiResult result = caregiverService.addCustomer(1L, 100L);

        assertEquals(500, result.getCode());
        assertEquals("添加失败", result.getMessage());
    }

    @Test
    @DisplayName("removeCustomer — 正常移除，返回成功")
    void removeCustomer_正常移除_返回成功() {
        when(userMapper.removeCustomer(eq(1L), eq(100L))).thenReturn(1);

        ApiResult result = caregiverService.removeCustomer(1L, 100L);

        assertEquals(200, result.getCode());
        assertEquals("删除成功", result.getMessage());
    }

    @Test
    @DisplayName("homeStats — 正常请求，返回完整首页统计数据")
    void homeStats_正常请求_返回完整首页数据() {
        try (MockedStatic<JWTUtil> jwtMock = mockStatic(JWTUtil.class)) {
            Map<String, Claim> claims = Map.of(
                    "userId", mockClaim("1"),
                    "email", mockClaim("test@test.com")
            );
            jwtMock.when(() -> JWTUtil.getPayloadFromToken(eq("valid"))).thenReturn(claims);

            when(nursingRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(5L, 3L);
            when(userMapper.getSumExecutedTimes(eq(1L))).thenReturn(150L);
            when(userMapper.getTotalCareCount(eq(1L))).thenReturn(20L);
            when(customerMapper.selectCount(any(QueryWrapper.class))).thenReturn(10L);
            when(nursingRecordMapper.countMonthCareCustomer(anyLong(), anyString())).thenReturn(8L);
            when(outingRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(2L, 1L, 3L, 0L);
            when(checkOutRecordMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L, 0L, 2L, 0L);

            ApiResult<HomeVO> result = caregiverService.homeStats("Bearer valid");

            assertEquals(200, result.getCode());
            assertEquals("查询成功", result.getMessage());
            HomeVO data = result.getData();
            assertNotNull(data);
            assertEquals(5L, data.getDailyCareCount());
            assertEquals(150L, data.getTotalCareCount());
            assertEquals(10L, data.getTotalCaredPeople());
            assertNotNull(data.getOutingApplicationStatus());
            assertNotNull(data.getCheckoutApplicationStatus());
        }
    }

    @Test
    @DisplayName("homeStats — 无效token，返回401")
    void homeStats_无效token_返回401() {
        ApiResult<HomeVO> result = caregiverService.homeStats("invalid-token-without-bearer");

        assertEquals(401, result.getCode());
    }

    /** 构造 mock Claim */
    private static Claim mockClaim(String value) {
        return mock(Claim.class, invocation -> {
            if ("asString".equals(invocation.getMethod().getName())) {
                return value;
            }
            return invocation.callRealMethod();
        });
    }
}
