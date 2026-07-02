package com.eldercare.system.service;

import com.auth0.jwt.interfaces.Claim;
import com.eldercare.system.dto.nursing.*;
import com.eldercare.system.entity.*;
import com.eldercare.system.mapper.*;
import com.eldercare.system.service.impl.NursingServiceImpl;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.vo.nursing.ItemListVO;
import com.eldercare.system.vo.nursing.LevelListVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 护理管理业务单元测试。
 * 覆盖 addRecord 原子更新、级别/项目管理、续期等核心流程。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("护理管理 Service 单元测试")
class NursingServiceTest {

    @Mock
    private NursingLevelMapper nursingLevelMapper;
    @Mock
    private NursingItemMapper nursingItemMapper;
    @Mock
    private NursingLevelItemMappingMapper nursingLevelItemMappingMapper;
    @Mock
    private NursingItemRecordMapper nursingItemRecordMapper;
    @Mock
    private NursingRecordMapper nursingRecordMapper;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private NursingServiceImpl nursingService;

    // ======================== addRecord ========================

    @Test
    @DisplayName("addRecord — 正常执行，更新executedTimes并插入记录")
    void addRecord_正常执行_更新次数并插入记录() {
        try (MockedStatic<JWTUtil> jwtMock = mockStatic(JWTUtil.class)) {
            Map<String, Claim> claims = Map.of("userId", mockClaim("1"));
            jwtMock.when(() -> JWTUtil.getPayloadFromToken(eq("valid"))).thenReturn(claims);

            NursingRecordRequest request = new NursingRecordRequest();
            request.setItemId(1L);
            request.setCustomerId(100L);
            request.setCode("N001");
            request.setName("血压测量");
            request.setNursingTime("2026-07-02");
            request.setTimes(2);

            NursingItemRecord itemRecord = new NursingItemRecord();
            itemRecord.setNursingItemRecordId(1L);
            itemRecord.setCustomerId(100L);
            itemRecord.setExecutionTimes(3);
            itemRecord.setExecutedTimes(1);
            itemRecord.setPurchasingTimes(2);
            itemRecord.setDelFlag("0");
            when(nursingItemRecordMapper.selectById(eq(1L))).thenReturn(itemRecord);
            when(nursingItemRecordMapper.updateTimesByRecordId(eq(1L), eq(2))).thenReturn(1);
            when(nursingRecordMapper.insert(any(NursingRecord.class))).thenReturn(1);

            ApiResult result = nursingService.addRecord(request, "Bearer valid");

            assertEquals(200, result.getCode());
            assertEquals("添加成功", result.getMessage());
            verify(nursingItemRecordMapper).updateTimesByRecordId(eq(1L), eq(2));
            verify(nursingRecordMapper).insert(any(NursingRecord.class));
        }
    }

    @Test
    @DisplayName("addRecord — 超出剩余次数，返回错误")
    void addRecord_超出剩余次数_返回错误() {
        NursingRecordRequest request = new NursingRecordRequest();
        request.setItemId(1L);
        request.setCustomerId(100L);
        request.setTimes(7);

        NursingItemRecord itemRecord = new NursingItemRecord();
        itemRecord.setCustomerId(100L);
        itemRecord.setExecutionTimes(3);
        itemRecord.setExecutedTimes(0);
        itemRecord.setPurchasingTimes(2);
        itemRecord.setDelFlag("0");
        when(nursingItemRecordMapper.selectById(eq(1L))).thenReturn(itemRecord);

        ApiResult result = nursingService.addRecord(request, "Bearer token");

        assertEquals(400, result.getCode());
        assertEquals("护理次数不能超过剩余次数", result.getMessage());
        verify(nursingItemRecordMapper, never()).updateTimesByRecordId(anyLong(), anyInt());
    }

    @Test
    @DisplayName("addRecord — times为null，返回参数错误")
    void addRecord_times为null_返回参数错误() {
        NursingRecordRequest request = new NursingRecordRequest();
        request.setTimes(null);

        ApiResult result = nursingService.addRecord(request, "Bearer token");

        assertEquals(400, result.getCode());
        assertEquals("护理次数必须大于0", result.getMessage());
    }

    @Test
    @DisplayName("addRecord — times为零，返回参数错误")
    void addRecord_times为零_返回参数错误() {
        NursingRecordRequest request = new NursingRecordRequest();
        request.setTimes(0);

        ApiResult result = nursingService.addRecord(request, "Bearer token");

        assertEquals(400, result.getCode());
        assertEquals("护理次数必须大于0", result.getMessage());
    }

    @Test
    @DisplayName("addRecord — 护理项目记录不存在，返回404")
    void addRecord_项目记录不存在_返回404() {
        NursingRecordRequest request = new NursingRecordRequest();
        request.setItemId(999L);
        request.setCustomerId(100L);
        request.setTimes(1);
        when(nursingItemRecordMapper.selectById(eq(999L))).thenReturn(null);

        ApiResult result = nursingService.addRecord(request, "Bearer token");

        assertEquals(404, result.getCode());
        assertEquals("客户护理项目不存在", result.getMessage());
    }

    // ======================== deleteItem ========================

    @Test
    @DisplayName("deleteItem — 软删除，同时删除级别项目映射")
    void deleteItem_软删除_设置delFlag为1() {
        ApiResult result = nursingService.deleteItem(1L);

        assertEquals(200, result.getCode());
        assertEquals("删除成功", result.getMessage());
        verify(nursingLevelItemMappingMapper).removeByItemId(eq(1L));
        verify(nursingItemMapper).removeByItemId(eq(1L));
    }

    // ======================== addLevelItem ========================

    @Test
    @DisplayName("addLevelItem — 正常添加，插入映射记录")
    void addLevelItem_正常添加_插入映射记录() {
        LevelItemRequest params = new LevelItemRequest();
        params.setLevelId(1L);
        params.setItemId(10L);
        when(nursingLevelItemMappingMapper.selectList(any())).thenReturn(new ArrayList<>());
        when(nursingLevelItemMappingMapper.insert(any())).thenReturn(1);

        ApiResult result = nursingService.addLevelItem(params);

        assertEquals(200, result.getCode());
        assertEquals("添加成功", result.getMessage());
        verify(nursingLevelItemMappingMapper).insert(any(NursingLevelItemMapping.class));
    }

    @Test
    @DisplayName("addLevelItem — 映射已存在，返回502")
    void addLevelItem_映射已存在_返回502() {
        LevelItemRequest params = new LevelItemRequest();
        params.setLevelId(1L);
        params.setItemId(10L);
        List<NursingLevelItemMapping> existing = List.of(new NursingLevelItemMapping());
        when(nursingLevelItemMappingMapper.selectList(any())).thenReturn(existing);

        ApiResult result = nursingService.addLevelItem(params);

        assertEquals(502, result.getCode());
        assertEquals("此护理级别和护理项目已存在映射关系", result.getMessage());
    }

    // ======================== renew ========================

    @Test
    @DisplayName("renew — 正常续期，调用mapper增加购买次数")
    void renew_正常续期_增加purchasingTimes() {
        RenewRequest params = new RenewRequest();
        params.setCustomerId(1L);
        params.setItemId(10L);
        params.setPurchasingTimes(2);
        params.setExpireDate("2027-12-31");

        ApiResult result = nursingService.renew(params);

        assertEquals(200, result.getCode());
        verify(nursingItemMapper).renew(params);
    }

    // ======================== getNursingLevelList ========================

    @Test
    @DisplayName("getNursingLevelList — 正常查询，返回级别分页列表")
    void getNursingLevelList_正常查询_返回级别列表() {
        LevelListRequest params = new LevelListRequest();
        params.setStatus("启用");
        params.setPageNum(1);
        params.setPageSize(10);

        NursingLevel level = new NursingLevel();
        level.setNursingLevelId(1L);
        level.setNursingLevelName("一级护理");
        doReturn(List.of(level)).when(nursingLevelMapper).selectList(any(com.baomidou.mybatisplus.core.metadata.IPage.class), any(com.baomidou.mybatisplus.core.conditions.Wrapper.class));
        when(nursingLevelMapper.selectCount(any())).thenReturn(1L);

        ApiResult<LevelListVO> result = nursingService.getNursingLevelList(params);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().getRecords().size());
        assertEquals(1L, result.getData().getTotal());
    }

    // ======================== updateLevel ========================

    @Test
    @DisplayName("updateLevel — 级别不存在，返回502")
    void updateLevel_级别不存在_返回502() {
        LevelRequest params = new LevelRequest();
        params.setId(999L);
        params.setStatus("启用");
        when(nursingLevelMapper.selectById(eq(999L))).thenReturn(null);

        ApiResult result = nursingService.updateLevel(params);

        assertEquals(502, result.getCode());
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
