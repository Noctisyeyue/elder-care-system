package com.eldercare.system.controller;

import com.eldercare.system.dto.customer.CustomerRegisterRequest;
import com.eldercare.system.dto.customer.CustomerListRequest;
import com.eldercare.system.service.CustomerService;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.customer.CustomerListVO;
import com.eldercare.system.vo.customer.CustomerVO;
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
 * 客户管理控制器单元测试。
 * 使用 standalone setup，Mock Service 层。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("客户管理 Controller 单元测试")
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    @DisplayName("GET /customer/list — 正常分页查询")
    void list_正常请求_返回200和分页数据() throws Exception {
        CustomerListVO listVO = new CustomerListVO();
        CustomerVO vo = new CustomerVO();
        vo.setId(1L);
        vo.setCustomerName("张三");
        listVO.setRecords(List.of(vo));
        listVO.setTotal(1L);
        ApiResult<CustomerListVO> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("查询成功");
        apiResult.setData(listVO);
        when(customerService.list(any(CustomerListRequest.class))).thenReturn(apiResult);

        mockMvc.perform(get("/customer/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("customerName", "张"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].customerName").value("张三"));
    }

    @Test
    @DisplayName("POST /customer/register — 有效请求返回200")
    void register_有效请求_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("登记成功");
        when(customerService.register(any(CustomerRegisterRequest.class))).thenReturn(apiResult);

        CustomerRegisterRequest request = new CustomerRegisterRequest();
        request.setCustomerName("张三");
        request.setGender("male");
        request.setIdNumber("110101199001011234");
        request.setBuildingNumber("A");
        request.setRoomNumber("101");
        request.setBedNumber("1");
        request.setCheckInDate("2026-07-01");
        request.setContractEndDate("2027-07-01");
        request.setCustomerType("1");

        mockMvc.perform(post("/customer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登记成功"));
    }

    @Test
    @DisplayName("DELETE /customer/{id} — 有效ID返回200")
    void delete_有效ID_返回200() throws Exception {
        ApiResult apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("删除成功");
        when(customerService.delete(eq(1L))).thenReturn(apiResult);

        mockMvc.perform(delete("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("GET /customer/count — 返回在住客户总数")
    void count_正常请求_返回总数() throws Exception {
        ApiResult<Long> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setData(42L);
        when(customerService.count()).thenReturn(apiResult);

        mockMvc.perform(get("/customer/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(42));
    }
}
