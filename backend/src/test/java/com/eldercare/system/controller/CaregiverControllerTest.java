package com.eldercare.system.controller;

import com.eldercare.system.dto.user.UserListRequest;
import com.eldercare.system.service.CaregiverService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.caregiver.CaregiverListVO;
import com.eldercare.system.vo.caregiver.CaregiverVO;
import com.eldercare.system.vo.caregiver.HomeVO;
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

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 健康管家控制器单元测试。
 * 使用 standalone setup，Mock Service 层。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("健康管家 Controller 单元测试")
class CaregiverControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CaregiverService caregiverService;

    @InjectMocks
    private CaregiverController caregiverController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(caregiverController).build();
    }

    @Test
    @DisplayName("GET /caregiver/homeStats — 有效token返回首页数据")
    void homeStats_有效token_返回首页数据() throws Exception {
        HomeVO homeVO = new HomeVO();
        homeVO.setDailyCareCount(5L);
        homeVO.setTotalCareCount(150L);
        homeVO.setTotalCaredPeople(10L);
        ApiResult<HomeVO> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(homeVO);
        when(caregiverService.homeStats(eq("Bearer valid.token"))).thenReturn(apiResult);

        mockMvc.perform(get("/caregiver/homeStats")
                        .header("Authorization", "Bearer valid.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.dailyCareCount").value(5));
    }

    @Test
    @DisplayName("GET /caregiver/homeStats — 无效token返回401")
    void homeStats_无效token_返回401() throws Exception {
        ApiResult<HomeVO> apiResult = new ApiResult<>();
        apiResult.setCode(401);
        when(caregiverService.homeStats(eq("invalid"))).thenReturn(apiResult);

        mockMvc.perform(get("/caregiver/homeStats")
                        .header("Authorization", "invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("GET /caregiver/list — 正常请求返回分页列表")
    void list_正常请求_返回分页列表() throws Exception {
        CaregiverVO vo = new CaregiverVO();
        vo.setId(1L);
        vo.setName("张三");
        vo.setPhone("13800138000");
        CaregiverListVO listVO = new CaregiverListVO(List.of(vo), 1);
        ApiResult<CaregiverListVO> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("查询成功");
        apiResult.setData(listVO);
        when(caregiverService.list(any(UserListRequest.class))).thenReturn(apiResult);

        mockMvc.perform(get("/caregiver/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].name").value("张三"));
    }

    @Test
    @DisplayName("POST /caregiver/addCustomer — 有效参数返回200")
    void addCustomer_有效参数_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("添加成功");
        when(caregiverService.addCustomer(eq(1L), eq(100L))).thenReturn(apiResult);

        Map<String, Long> body = Map.of("caregiverId", 1L, "customerId", 100L);

        mockMvc.perform(post("/caregiver/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /caregiver/removeCustomer — 有效参数返回200")
    void removeCustomer_有效参数_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("删除成功");
        when(caregiverService.removeCustomer(eq(1L), eq(100L))).thenReturn(apiResult);

        Map<String, Long> body = Map.of("caregiverId", 1L, "customerId", 100L);

        mockMvc.perform(delete("/caregiver/removeCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
