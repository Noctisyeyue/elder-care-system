package com.eldercare.system.controller;

import com.eldercare.system.dto.nursing.NursingRecordRequest;
import com.eldercare.system.dto.nursing.LevelListRequest;
import com.eldercare.system.service.NursingService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.nursing.ItemListVO;
import com.eldercare.system.vo.nursing.LevelListVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 护理管理控制器单元测试。
 * 使用 standalone setup，Mock Service 层。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("护理管理 Controller 单元测试")
class NursingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NursingService nursingService;

    @InjectMocks
    private NursingController nursingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(nursingController).build();
    }

    @Test
    @DisplayName("GET /nursing/level/list — 返回护理级别分页列表")
    void levelList_正常请求_返回级别列表() throws Exception {
        LevelListVO listVO = new LevelListVO();
        listVO.setRecords(new ArrayList<>());
        listVO.setTotal(0L);
        ApiResult<LevelListVO> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(listVO);
        when(nursingService.getNursingLevelList(any(LevelListRequest.class))).thenReturn(apiResult);

        mockMvc.perform(get("/nursing/level/list")
                        .param("status", "启用")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /nursing/item/list — 返回护理项目分页列表")
    void itemList_正常请求_返回项目列表() throws Exception {
        ItemListVO listVO = new ItemListVO();
        listVO.setRecords(new ArrayList<>());
        listVO.setTotal(0L);
        ApiResult<ItemListVO> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(listVO);
        when(nursingService.getNursingItemList(any())).thenReturn(apiResult);

        mockMvc.perform(get("/nursing/item/list")
                        .param("status", "启用")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /nursing/item/delete/{id} — 有效ID返回200")
    void deleteItem_有效ID_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("删除成功");
        when(nursingService.deleteItem(eq(1L))).thenReturn(apiResult);

        mockMvc.perform(delete("/nursing/item/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("POST /nursing/record/add — 无token时仍可请求")
    void addRecord_无token_可正常请求() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(401);
        apiResult.setMessage("Token解析失败");
        when(nursingService.addRecord(any(NursingRecordRequest.class), isNull())).thenReturn(apiResult);

        NursingRecordRequest request = new NursingRecordRequest();
        request.setItemId(1L);
        request.setCustomerId(100L);
        request.setTimes(2);

        mockMvc.perform(post("/nursing/record/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("POST /nursing/record/add — 带token正常请求返回200")
    void addRecord_有效请求_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("添加成功");
        when(nursingService.addRecord(any(NursingRecordRequest.class), eq("Bearer token"))).thenReturn(apiResult);

        NursingRecordRequest request = new NursingRecordRequest();
        request.setItemId(1L);
        request.setCustomerId(100L);
        request.setTimes(2);

        mockMvc.perform(post("/nursing/record/add")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
