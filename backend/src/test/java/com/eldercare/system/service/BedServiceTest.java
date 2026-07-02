package com.eldercare.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.dto.bed.BedListRequest;
import com.eldercare.system.dto.bed.BedSwapRequest;
import com.eldercare.system.entity.Bed;
import com.eldercare.system.entity.BedRecord;
import com.eldercare.system.entity.Customer;
import com.eldercare.system.mapper.BedMapper;
import com.eldercare.system.mapper.BedRecordMapper;
import com.eldercare.system.mapper.CustomerMapper;
import com.eldercare.system.mapper.RoomMapper;
import com.eldercare.system.service.impl.BedServiceImpl;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.bed.BedListVO;
import com.eldercare.system.vo.bed.BedStatusDistributionVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 床位管理业务单元测试。
 * 覆盖换床、状态分布统计、列表查询、示意图查询等核心流程。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("床位管理 Service 单元测试")
class BedServiceTest {

    @Mock
    private BedMapper bedMapper;
    @Mock
    private BedRecordMapper bedRecordMapper;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private BedServiceImpl bedService;

    // ======================== statusDistribution ========================

    @Test
    @DisplayName("statusDistribution — 正常查询，返回四类计数")
    void statusDistribution_正常查询_返回四类计数() {
        when(bedMapper.selectCount(any(QueryWrapper.class))).thenReturn(100L, 30L, 60L, 10L);

        ApiResult<BedStatusDistributionVO> result = bedService.statusDistribution();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(100L, result.getData().getTotal());
        assertEquals(30L, result.getData().getFree());
        assertEquals(60L, result.getData().getUsed());
        assertEquals(10L, result.getData().getOut());
        verify(bedMapper, times(4)).selectCount(any(QueryWrapper.class));
    }

    // ======================== getList ========================

    @Test
    @DisplayName("getList — 带入住日期查询，返回分页数据")
    void getList_带入住日期查询_返回分页数据() {
        BedListRequest request = new BedListRequest();
        request.setCustomerName("张三");
        request.setCheckInDate("2026-07-01");
        request.setHistory(1);
        request.setPageNum(1);
        request.setPageSize(10);

        List<Map<String, Object>> dbBeds = new ArrayList<>();
        Map<String, Object> bedMap = new HashMap<>();
        bedMap.put("bed_record_id", 1L);
        bedMap.put("customer_name", "张三");
        bedMap.put("gender", "男");
        bedMap.put("usage_start_date", "2026-07-01");
        bedMap.put("usage_end_date", "2027-07-01");
        bedMap.put("history", "1");
        bedMap.put("status", "used");
        bedMap.put("building", "A");
        bedMap.put("room_no", "101");
        bedMap.put("bed_no", "1");
        dbBeds.add(bedMap);
        when(bedMapper.selectBedsByCustomerNameAndCheckInDateAndHistory(anyMap())).thenReturn(dbBeds);

        ApiResult<BedListVO> result = bedService.getList(request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    @DisplayName("getList — 无入住日期，走另一查询分支")
    void getList_无入住日期_走另一分支() {
        BedListRequest request = new BedListRequest();
        request.setPageNum(1);
        request.setPageSize(10);
        when(bedMapper.selectBedsByCustomerNameAndHistory(anyMap())).thenReturn(new ArrayList<>());

        ApiResult<BedListVO> result = bedService.getList(request);

        assertEquals(200, result.getCode());
        assertEquals(0, result.getData().getTotal());
    }

    // ======================== getMap ========================

    @Test
    @DisplayName("getMap — 按楼层和楼栋查询，组装房间床位树")
    void getMap_按楼层查询_返回房间床位树() {
        Map<String, Object> roomMap = new HashMap<>();
        roomMap.put("room_id", 1);
        roomMap.put("room_no", "101");
        List<Map<String, Object>> dbRooms = List.of(roomMap);
        when(bedMapper.selectRoomsByFloor(anyMap())).thenReturn(dbRooms);

        Map<String, Object> bedMap = new HashMap<>();
        bedMap.put("bed_no", "1");
        bedMap.put("status", "free");
        bedMap.put("bed_id", 10L);
        List<Map<String, Object>> dbBeds = List.of(bedMap);
        when(bedMapper.selectBedsByRoom(eq(1))).thenReturn(dbBeds);

        Customer customer = new Customer();
        customer.setCustomerName("张三");
        customer.setGender("男");
        customer.setAge(80);
        customer.setCheckInDate("2026-07-01");
        customer.setContractEndDate("2027-07-01");
        when(customerMapper.selectOne(any(QueryWrapper.class))).thenReturn(customer);

        ApiResult<List<com.eldercare.system.vo.bed.BedMapVO>> result = bedService.getMap("1", "A");

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("101", result.getData().get(0).getRoomNumber());
    }

    // ======================== swap ========================

    @Test
    @DisplayName("swap — 正常交换，旧床释放新床占用")
    void swap_正常交换_旧床释放新床占用() {
        BedSwapRequest request = buildSwapRequest();

        BedRecord oldRecord = new BedRecord();
        oldRecord.setBedRecordId(1L);
        oldRecord.setBedId(10L);
        oldRecord.setCustomerId(100L);
        oldRecord.setHistory("1");
        oldRecord.setDelFlag("0");
        when(bedRecordMapper.selectOne(any(QueryWrapper.class))).thenReturn(oldRecord);
        when(bedRecordMapper.updateById(any(BedRecord.class))).thenReturn(1);

        Bed oldBed = new Bed();
        oldBed.setBedId(10L);
        oldBed.setStatus("used");
        when(bedMapper.selectById(eq(10L))).thenReturn(oldBed);
        when(bedMapper.updateById(any(Bed.class))).thenReturn(1);

        Bed newBed = new Bed();
        newBed.setBedId(20L);
        newBed.setBedNo("2");
        newBed.setStatus("free");
        when(bedMapper.selectBedByBedDetails(anyMap())).thenReturn(newBed);

        when(bedRecordMapper.insert(any(BedRecord.class))).thenReturn(1);

        Customer customer = new Customer();
        customer.setCustomerId(100L);
        customer.setBedId(10L);
        when(customerMapper.selectById(eq(100L))).thenReturn(customer);
        when(customerMapper.updateById(any(Customer.class))).thenReturn(1);

        ApiResult result = bedService.swap(request);

        assertEquals(200, result.getCode());
        assertEquals("修改成功", result.getMessage());
        assertEquals("0", oldRecord.getHistory());
        assertEquals("free", oldBed.getStatus());
        assertEquals("used", newBed.getStatus());
        assertEquals(20L, customer.getBedId());
    }

    @Test
    @DisplayName("swap — 旧床位无活跃记录，NPE被抛出")
    void swap_旧床位无活跃记录_异常() {
        BedSwapRequest request = buildSwapRequest();
        when(bedRecordMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        // selectOne 返回 null → oldBedRecord.setHistory("0") 抛 NPE（不在 try 块内）

        assertThrows(NullPointerException.class, () -> bedService.swap(request));
    }

    // ======================== selectFreeBeds ========================

    @Test
    @DisplayName("selectFreeBeds — 正常查询，返回空闲床位选项")
    void selectFreeBeds_正常查询_返回空闲床位列表() {
        Map<String, Object> bedMap = new HashMap<>();
        bedMap.put("bed_no", "1");
        List<Map<String, Object>> dbBeds = List.of(bedMap);
        when(bedMapper.selectFreeBedsByRoom(anyMap())).thenReturn(dbBeds);

        ApiResult<List<com.eldercare.system.vo.bed.PairVO>> result = bedService.selectFreeBeds("101", "A");

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
        assertEquals("1", result.getData().get(0).getValue());
    }

    // ======================== helper ========================

    private BedSwapRequest buildSwapRequest() {
        BedSwapRequest request = new BedSwapRequest();
        request.setOldBedId(1L);
        request.setOldBedEndDate("2026-07-02");
        request.setNewBuildingNumber("B");
        request.setNewRoomNumber("202");
        request.setNewBedNumber("2");
        request.setNewBedStartDate("2026-07-03");
        request.setNewBedEndDate("2027-07-03");
        return request;
    }
}
