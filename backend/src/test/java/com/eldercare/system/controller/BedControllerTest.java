package com.eldercare.system.controller;

import com.eldercare.system.dto.bed.BedSwapRequest;
import com.eldercare.system.service.BedService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.bed.BedStatusDistributionVO;
import com.eldercare.system.vo.bed.PairVO;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 床位管理控制器单元测试。
 * 使用 standalone setup，Mock Service 层。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("床位管理 Controller 单元测试")
class BedControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BedService bedService;

    @InjectMocks
    private BedController bedController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bedController).build();
    }

    @Test
    @DisplayName("GET /bed/statusDistribution — 返回床位状态分布")
    void statusDistribution_正常请求_返回床位分布() throws Exception {
        BedStatusDistributionVO vo = new BedStatusDistributionVO();
        vo.setFree(30L);
        vo.setUsed(60L);
        vo.setOut(10L);
        vo.setTotal(100L);
        ApiResult<BedStatusDistributionVO> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(vo);
        when(bedService.statusDistribution()).thenReturn(apiResult);

        mockMvc.perform(get("/bed/statusDistribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.free").value(30))
                .andExpect(jsonPath("$.data.used").value(60))
                .andExpect(jsonPath("$.data.total").value(100));
    }

    @Test
    @DisplayName("POST /bed/swap — 有效请求返回200")
    void swap_有效请求_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("修改成功");
        when(bedService.swap(any(BedSwapRequest.class))).thenReturn(apiResult);

        BedSwapRequest request = new BedSwapRequest();
        request.setOldBedId(1L);
        request.setOldBedEndDate("2026-07-02");
        request.setNewBuildingNumber("B");
        request.setNewRoomNumber("202");
        request.setNewBedNumber("2");
        request.setNewBedStartDate("2026-07-03");
        request.setNewBedEndDate("2027-07-03");

        mockMvc.perform(post("/bed/swap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改成功"));
    }

    @Test
    @DisplayName("GET /bed/freeBeds/{roomNumber} — 返回空闲床位列表")
    void freeBeds_有效房间号_返回空闲床位() throws Exception {
        PairVO pair = new PairVO();
        pair.setValue("1");
        pair.setLabel("1号床");
        ApiResult<List<PairVO>> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(List.of(pair));
        when(bedService.selectFreeBeds(eq("101"), eq("A"))).thenReturn(apiResult);

        mockMvc.perform(get("/bed/freeBeds/101").param("building", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].value").value("1"));
    }

    @Test
    @DisplayName("GET /bed/floorList — 返回楼层列表")
    void floorList_正常请求_返回楼层() throws Exception {
        ApiResult<List<String>> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(List.of("1", "2", "3"));
        when(bedService.floorList(anyString())).thenReturn(apiResult);

        mockMvc.perform(get("/bed/floorList").param("building", "A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("1"));
    }
}
