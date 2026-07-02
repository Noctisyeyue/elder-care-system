package com.eldercare.system.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.eldercare.system.dto.bed.BedSwapRequest;
import com.eldercare.system.dto.customer.CustomerListRequest;
import com.eldercare.system.dto.customer.CustomerRegisterRequest;
import com.eldercare.system.dto.customer.OutingRequest;
import com.eldercare.system.entity.Bed;
import com.eldercare.system.entity.BedRecord;
import com.eldercare.system.entity.Customer;
import com.eldercare.system.entity.OutingRecord;
import com.eldercare.system.entity.Room;
import com.eldercare.system.mapper.BedMapper;
import com.eldercare.system.mapper.BedRecordMapper;
import com.eldercare.system.mapper.CheckOutRecordMapper;
import com.eldercare.system.mapper.CustomerMapper;
import com.eldercare.system.mapper.NursingRecordMapper;
import com.eldercare.system.mapper.OutingRecordMapper;
import com.eldercare.system.mapper.RoomMapper;
import com.eldercare.system.mapper.SetMealCustomerMappingMapper;
import com.eldercare.system.mapper.UserMapper;
import com.eldercare.system.service.impl.BedServiceImpl;
import com.eldercare.system.service.impl.CaregiverServiceImpl;
import com.eldercare.system.service.impl.CustomerServiceImpl;
import com.eldercare.system.service.impl.RoomServiceImpl;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.util.JWTUtil;
import com.eldercare.system.vo.customer.CustomerListVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 客户管理业务单元测试。
 * 覆盖入住登记、删除、更新、外出申请等核心流程。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("客户管理 Service 单元测试")
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private BedMapper bedMapper;
    @Mock
    private BedRecordMapper bedRecordMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private OutingRecordMapper outingRecordMapper;
    @Mock
    private NursingRecordMapper nursingRecordMapper;
    @Mock
    private SetMealCustomerMappingMapper setMealCustomerMappingMapper;
    @Mock
    private BedServiceImpl bedService;
    @Mock
    private RoomServiceImpl roomService;
    @Mock
    private CaregiverServiceImpl caregiverServiceImpl;
    /** 重复注入的三个同类型字段，均指向 CheckOutRecordMapper */
    @Mock
    private CheckOutRecordMapper checkOutRecordMapper;
    @Mock(name = "checkoutMapper")
    private CheckOutRecordMapper checkoutMapper;
    @Mock(name = "checkoutRecordMapper")
    private CheckOutRecordMapper checkoutRecordMapperMock;

    @InjectMocks
    private CustomerServiceImpl customerService;

    // ======================== list ========================

    @Test
    @DisplayName("list — 有数据时返回分页列表")
    void list_有匹配数据_返回分页列表() {
        CustomerListRequest request = new CustomerListRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        List<com.eldercare.system.vo.customer.CustomerVO> items = new ArrayList<>();
        com.eldercare.system.vo.customer.CustomerVO vo = new com.eldercare.system.vo.customer.CustomerVO();
        vo.setId(1L);
        vo.setCustomerName("张三");
        items.add(vo);
        when(customerMapper.listCustomerItems(isNull(), isNull(), eq(0), eq(10))).thenReturn(items);
        when(customerMapper.countCustomerItems(isNull(), isNull())).thenReturn(1L);

        ApiResult<CustomerListVO> result = customerService.list(request);

        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getRecords().size());
    }

    @Test
    @DisplayName("list — 无数据时返回空列表")
    void list_无匹配数据_返回空列表() {
        CustomerListRequest request = new CustomerListRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        when(customerMapper.listCustomerItems(isNull(), isNull(), eq(0), eq(10))).thenReturn(new ArrayList<>());
        when(customerMapper.countCustomerItems(isNull(), isNull())).thenReturn(0L);

        ApiResult<CustomerListVO> result = customerService.list(request);

        assertEquals(200, result.getCode());
        assertEquals("数据为空", result.getMessage());
    }

    // ======================== register ========================

    @Test
    @DisplayName("register — 正常参数，返回成功并占用床位")
    void register_正常参数_返回成功并占用床位() {
        CustomerRegisterRequest param = buildValidRegisterRequest();
        Bed bed = new Bed();
        bed.setBedId(100L);
        bed.setBedNo("1");
        when(bedMapper.selectBedByBedDetails(anyMap())).thenReturn(bed);
        when(customerMapper.selectByIdNum(eq("110101199001011234"))).thenReturn(null);
        when(customerMapper.insert(any(Customer.class))).thenReturn(1);
        doNothing().when(bedMapper).updateBedStatusUsed(eq(100L));
        when(customerMapper.getIdByIdNum(eq("110101199001011234"))).thenReturn(200L);
        when(bedRecordMapper.insert(any(BedRecord.class))).thenReturn(1);

        ApiResult result = customerService.register(param);

        assertEquals(200, result.getCode());
        assertEquals("登记成功", result.getMessage());
        verify(bedMapper).updateBedStatusUsed(eq(100L));
        verify(bedRecordMapper).insert(any(BedRecord.class));
    }

    @Test
    @DisplayName("register — 身份证号重复，返回错误")
    void register_重复身份证号_返回错误() {
        CustomerRegisterRequest param = buildValidRegisterRequest();
        Bed bed = new Bed();
        bed.setBedId(100L);
        when(bedMapper.selectBedByBedDetails(anyMap())).thenReturn(bed);
        Customer existing = new Customer();
        existing.setCustomerId(999L);
        when(customerMapper.selectByIdNum(eq("110101199001011234"))).thenReturn(existing);

        ApiResult result = customerService.register(param);

        assertEquals(500, result.getCode());
        assertEquals("添加失败，用户已存在", result.getMessage());
        verify(customerMapper, never()).insert(any());
    }

    @Test
    @DisplayName("register — 出生日期格式不正确，返回错误")
    void register_日期格式错误_返回错误() {
        CustomerRegisterRequest param = buildValidRegisterRequest();
        param.setDateOfBirth("2020/01/01");

        ApiResult result = customerService.register(param);

        assertEquals("出生日期格式不正确", result.getMessage());
    }

    // ======================== delete ========================

    @Test
    @DisplayName("delete — 正常删除，释放床位并软删除关联记录")
    void delete_正常删除_释放床位并软删除关联记录() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setBedId(10L);
        when(customerMapper.selectById(eq(1L))).thenReturn(customer);

        ApiResult result = customerService.delete(1L);

        assertEquals(200, result.getCode());
        assertEquals("删除成功", result.getMessage());
        verify(bedMapper).updateBedStatus(eq(10L));
        verify(bedRecordMapper).updateBedRecordUsageEndDate(eq(1L), anyString(), eq("0"));
        verify(nursingRecordMapper).updateNursingRecordDelFlag(eq(1L));
    }

    @Test
    @DisplayName("delete — 客户不存在，数据库异常被捕获")
    void delete_客户不存在_数据库异常() {
        when(customerMapper.selectById(eq(999L))).thenReturn(null);
        // selectById 返回 null → getBedId() 抛 NPE → 被 catch 捕获，设置 code=500 并 re-throw

        assertThrows(Exception.class, () -> customerService.delete(999L));
    }

    // ======================== update ========================

    @Test
    @DisplayName("update — 客户不存在，返回404")
    void update_客户不存在_返回404() {
        CustomerRegisterRequest param = buildValidRegisterRequest();
        when(customerMapper.selectById(eq(999L))).thenReturn(null);

        ApiResult result = customerService.update(999L, param);

        assertEquals(404, result.getCode());
        assertEquals("客户不存在", result.getMessage());
    }

    @Test
    @DisplayName("update — 换床操作，调用swap并更新客户信息")
    void update_换床操作_调用swap() {
        CustomerRegisterRequest param = buildValidRegisterRequest();
        param.setBuildingNumber("B");
        param.setRoomNumber("202");
        param.setBedNumber("3");
        Customer existing = new Customer();
        existing.setCustomerId(1L);
        existing.setBedId(10L);
        existing.setCustomerName("张三");
        when(customerMapper.selectById(eq(1L))).thenReturn(existing);
        when(bedRecordMapper.selectBedRecordIdByBedIdAndHistory(eq(10L), eq("1"))).thenReturn(50L);
        Bed bed = new Bed();
        bed.setBedId(10L);
        bed.setBedNo("1");
        bed.setRoomId(5L);
        when(bedMapper.selectById(eq(10L))).thenReturn(bed);
        Room room = new Room();
        room.setBuilding("A");
        room.setRoomNo("101");
        when(roomMapper.selectById(eq(5L))).thenReturn(room);
        Bed newBed = new Bed();
        newBed.setBedId(20L);
        when(bedMapper.selectBedByBedDetails(anyMap())).thenReturn(newBed);
        BedRecord oldRecord = new BedRecord();
        oldRecord.setBedId(10L);
        when(bedRecordMapper.selectByBedId(eq(10L))).thenReturn(oldRecord);
        when(customerMapper.updateById(any(Customer.class))).thenReturn(1);

        ApiResult result = customerService.update(1L, param);

        verify(bedService).swap(any(BedSwapRequest.class));
        verify(customerMapper).updateById(any(Customer.class));
        assertEquals(200, result.getCode());
    }

    // ======================== outingApply ========================

    @Test
    @DisplayName("outingApply — 正常申请，创建外出记录")
    void outingApply_正常申请_创建外出记录() {
        try (MockedStatic<JWTUtil> jwtMock = mockStatic(JWTUtil.class)) {
            Map<String, Claim> claims = Map.of(
                    "userId", mockClaim("1")
            );
            jwtMock.when(() -> JWTUtil.getPayloadFromToken(eq("valid"))).thenReturn(claims);

            OutingRequest param = new OutingRequest();
            param.setCustomerId(1L);
            param.setOutingDate("2026-07-02");
            param.setOutingReason("回家探亲");
            param.setReturnDate("2026-07-05");
            String token = "Bearer valid";

            Customer customer = new Customer();
            customer.setCustomerId(1L);
            when(customerMapper.selectById(eq(1L))).thenReturn(customer);
            when(outingRecordMapper.insert(any(OutingRecord.class))).thenReturn(1);

            ApiResult result = customerService.outingApply(param, token);

            assertEquals(200, result.getCode());
            assertEquals("申请成功", result.getMessage());
            verify(outingRecordMapper).insert(any(OutingRecord.class));
        }
    }

    // ======================== helper ========================

    private CustomerRegisterRequest buildValidRegisterRequest() {
        CustomerRegisterRequest param = new CustomerRegisterRequest();
        param.setCustomerName("张三");
        param.setGender("male");
        param.setDateOfBirth("1990-01-10");
        param.setIdNumber("110101199001011234");
        param.setBloodType("A");
        param.setFamilyMember("李四");
        param.setTel("13800138000");
        param.setFamilyMemberTel("13900139000");
        param.setNation("汉族");
        param.setBuildingNumber("A");
        param.setRoomNumber("101");
        param.setBedNumber("1");
        param.setCheckInDate("2026-07-01");
        param.setContractEndDate("2027-07-01");
        param.setPhysicalMentalStatus("健康");
        param.setCustomerType("1");
        return param;
    }

    /**
     * 构造 mock Claim 对象（使用反射避免直接依赖 auth0 内部实现）。
     */
    @SuppressWarnings("unchecked")
    private static Claim mockClaim(String value) {
        try {
            // Claim 构造函数接受实际的 JSON 解析结果，此处通过反射创建简单的 mock
            return mock(Claim.class, invocation -> {
                if ("asString".equals(invocation.getMethod().getName())) {
                    return value;
                }
                return invocation.callRealMethod();
            });
        } catch (Exception e) {
            return null;
        }
    }
}
