
package com.eldercare.system.service.impl;
import com.eldercare.system.service.BedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.entity.Bed;
import com.eldercare.system.entity.BedRecord;
import com.eldercare.system.entity.Customer;
import com.eldercare.system.mapper.BedMapper;
import com.eldercare.system.mapper.BedRecordMapper;
import com.eldercare.system.mapper.CustomerMapper;
import com.eldercare.system.mapper.RoomMapper;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.bed.BedListRequest;
import com.eldercare.system.dto.bed.BedSwapRequest;
import com.eldercare.system.vo.bed.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 床位服务实现
 */
@Service
public class BedServiceImpl implements BedService{

    /** 床位 Mapper */
    @Autowired
    private BedMapper bedMapper;

    /** 床位使用记录 Mapper */
    @Autowired
    private BedRecordMapper bedRecordMapper;

    /** 房间 Mapper */
    @Autowired
    private RoomMapper roomMapper;

    /** 客户 Mapper */
    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查询床位示意图数据
     *
     * @param floor 楼层
     * @return 床位示意图数据
     */
    @Override
    public ApiResult<List<BedMapVO>> getMap(String floor, String building) {
        // 按层获取床位示意图
        // 变量准备
        ApiResult<List<BedMapVO>> result = new ApiResult<>();
        List<BedMapVO> list = new ArrayList<>();
        List<Map<String, Object>> dbRooms;
        Map<String, Object> floorParams = new HashMap<>();
        floorParams.put("floor", floor);
        floorParams.put("building", building);
        // 数据库查询
        // 按楼层查询房间
        try{
            dbRooms = bedMapper.selectRoomsByFloor(floorParams);
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("按楼查找房间数据库错误");
            throw e;
        }
        // 获取房间数据
        if(dbRooms == null || dbRooms.isEmpty()){
            result.setCode(200);
            result.setData(list);
            result.setMessage("查询成功");
            return result;
        }
        // 按房间编号查询床
        for(Map room : dbRooms){
            List<BedVO> details = new ArrayList<>();
            List<Map<String, Object>> dbBeds;
            try {
                dbBeds = bedMapper.selectBedsByRoom(room.get("room_id"));
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("按房间查找床数据库错误");
                throw e;
            }
            // 包装单个房间数据
            BedMapVO mapResult = new BedMapVO();
            mapResult.setRoomNumber(String.valueOf(room.get("room_no")));
            List<Bed> bedList = new ArrayList<>();
            for(Map bed : dbBeds){
                Bed bedData = new Bed();
                bedData.setBedNo(String.valueOf(bed.get("bed_no")));
                bedData.setStatus(String.valueOf(bed.get("status")));
                bedList.add(bedData);
                BedVO detail = new BedVO();
                Customer customer;
                QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("bed_id", bed.get("bed_id"));
                queryWrapper.eq("del_flag", "0");
                try {
                    customer = customerMapper.selectOne(queryWrapper);
                } catch (Exception e) {
                    result.setCode(500);
                    result.setMessage("按床查找客户数据库错误");
                    throw e;
                }
                if(customer != null){
                    detail.setCustomerName(customer.getCustomerName());
                    detail.setGender(customer.getGender());
                    detail.setAge(String.valueOf(customer.getAge()));
                    detail.setUsageStartDate(customer.getCheckInDate());
                    detail.setUsageEndDate(customer.getContractEndDate());
                } else {
                    detail.setCustomerName("无");
                    detail.setGender("无");
                    detail.setAge("无");
                    detail.setUsageStartDate("无");
                    detail.setUsageEndDate("无");
                }
                details.add(detail);
            }
            mapResult.setBeds(bedList);
            mapResult.setDetails(details);
            list.add(mapResult);
        }
        // 数据包装并返回
        result.setData(list);
        result.setCode(200);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 分页查询床位使用列表
     *
     * @param params 查询参数
     * @return 床位使用列表
     */
    @Override
    public ApiResult<BedListVO> getList(BedListRequest params) {
        // 按条件获取床位使用信息列表
        // 变量准备
        ApiResult<BedListVO> result = new ApiResult<>();
        BedListVO data = new BedListVO();
        List<BedVO> list = new ArrayList<>();
        List<Map<String, Object>> dbBeds;
        // 若传入参数有入住日期
        if(!(Objects.equals(params.getCheckInDate(), "") || params.getCheckInDate() == null)){
            // 查询参数准备
            Map<String, Object> paramsMap = new  HashMap<>();
            paramsMap.put("customerName", params.getCustomerName());
            paramsMap.put("checkInDate", params.getCheckInDate());
            paramsMap.put("history", params.getHistory());
            // 数据库查询
            try {
                dbBeds = bedMapper.selectBedsByCustomerNameAndCheckInDateAndHistory(paramsMap);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("按客户名和入住日期查找床数据库错误");
                throw e;
            }
            // 包装单个床数据
            for (Map<String, Object> bed : dbBeds){
                BedVO bedResult = new BedVO();
                bedResult.setId((Long) bed.get("bed_record_id"));
                bedResult.setCustomerName(String.valueOf(bed.get("customer_name")));
                bedResult.setGender(String.valueOf(bed.get("gender")));
                bedResult.setUsageStartDate(String.valueOf(bed.get("usage_start_date")));
                bedResult.setUsageEndDate(String.valueOf(bed.get("usage_end_date")));
                bedResult.setHistory(String.valueOf(bed.get("history")));
                bedResult.setStatus(String.valueOf(bed.get("status")));
                bedResult.setBuildingNumber(String.valueOf(bed.get("building")));
                bedResult.setRoomNumber(String.valueOf(bed.get("room_no")));
                bedResult.setBedNumber(String.valueOf(bed.get("bed_no")));
                list.add(bedResult);
            }
        }else{
            // 查询参数准备
            Map<String, Object> paramsMap = new  HashMap<>();
            paramsMap.put("customerName", params.getCustomerName());
            paramsMap.put("history", params.getHistory());
            // 数据库查询
            try {
                dbBeds = bedMapper.selectBedsByCustomerNameAndHistory(paramsMap);
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("按客户名查找床数据库错误");
                throw e;
            }
            // 包装单个床数据
            if (dbBeds != null) {
                for (Map<String, Object> bed : dbBeds){
                    BedVO bedResult = new BedVO();
                    bedResult.setId((Long) bed.get("bed_record_id"));
                    bedResult.setCustomerName(String.valueOf(bed.get("customer_name")));
                    bedResult.setGender(String.valueOf(bed.get("gender")));
                    bedResult.setUsageStartDate(String.valueOf(bed.get("usage_start_date")));
                    bedResult.setUsageEndDate(String.valueOf(bed.get("usage_end_date")));
                    bedResult.setHistory(String.valueOf(bed.get("history")));
                    bedResult.setStatus(String.valueOf(bed.get("status")));
                    bedResult.setBuildingNumber(String.valueOf(bed.get("building")));
                    bedResult.setRoomNumber(String.valueOf(bed.get("room_no")));
                    bedResult.setBedNumber(String.valueOf(bed.get("bed_no")));
                    list.add(bedResult);
                }
            }
        }
        // 分页
        int totalItems = list.size();
        int fromIndex = (params.getPageNum() - 1) * params.getPageSize();
        int toIndex = Math.min(fromIndex + params.getPageSize(), totalItems);
        List<BedVO> page = list.subList(fromIndex,toIndex);
        // 数据包装并返回
        data.setList(page);
        data.setTotal(totalItems);
        result.setData(data);
        result.setCode(200);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 修改床位使用结束日期
     *
     * @param id     床位使用记录ID
     * @param params 修改参数
     * @return 操作结果
     */
    @Override
    public ApiResult updateUsageEndDateById(Long id, Map<String,String> params) {
        // 修改使用结束日期
        // 变量准备
        ApiResult result = new ApiResult();
        // 参数准备
        BedRecord bedRecord;
        // 数据库查询
        // 获取床位使用记录
        try {
            bedRecord = bedRecordMapper.selectById(id);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询床位使用记录数据库错误");
            throw e;
        }
        // 数据库修改床位使用记录结束日期
        bedRecord.setUsageEndDate(params.get("usageEndDate"));
        try {
            bedRecordMapper.updateById(bedRecord);
        } catch (Exception e){
            result.setCode(500);
            result.setMessage("修改床位使用记录结束日期数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("更新成功");
        return result;
    }

    /**
     * 查询有空闲床位的房间
     *
     * @return 空闲房间列表
     */
    @Override
    public ApiResult<List<FreeRoomVO>> selectFreeRooms(String building) {
        // 获取有空闲床位的房间
        // 变量准备
        ApiResult<List<FreeRoomVO>> result = new ApiResult<>();// 返回结果
        List<FreeRoomVO> data = new ArrayList<>();
        List<String> floorList;
        List<Map<String, Object>> dbFreeRooms;// 数据库查询结果
        // 数据库查询
        // 获取楼层列表
        try {
            floorList = roomMapper.getFloorListByBuilding(building);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找楼层列表数据库错误");
            throw e;
        }
        for (String floor : floorList){
            FreeRoomVO freeRoomsResult = new FreeRoomVO();
            freeRoomsResult.setLabel(floor);
            freeRoomsResult.setOptions(new ArrayList<>());
            data.add(freeRoomsResult);
        }
        // 获取有空闲床位的房间
        try {
            dbFreeRooms = bedMapper.selectFreeRooms(building);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找有空闲床位房间数据库错误");
            throw e;
        }
        // 将dbFreeRooms拆分并包装成前端所需数据格式
        for (Map<String, Object> room : dbFreeRooms) {
            for(String floor : floorList){
                if (room.get("floor").equals(floor)){
                    for(FreeRoomVO freeRoomsResult : data){
                        if (freeRoomsResult.getLabel().equals(floor)){
                            PairVO pair = new PairVO();
                            pair.setValue((String) room.get("room_no"));
                            pair.setLabel(room.get("room_no") + "房间");
                            freeRoomsResult.getOptions().add(pair);
                        }
                    }
                }
            }
        }
        // 数据包装并返回
        result.setData(data);
        result.setCode(200);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 查询指定房间的空闲床位
     *
     * @param roomNumber 房间号
     * @return 空闲床位列表
     */
    @Override
    public ApiResult<List<PairVO>> selectFreeBeds(String roomNumber, String building) {
        // 获取指定房间的空闲床位
        // 变量准备
        ApiResult<List<PairVO>> result = new ApiResult<>();
        List<PairVO> data = new ArrayList<>();
        List<Map<String, Object>> dbFreeBeds;
        Map<String, Object> params = new HashMap<>();
        params.put("roomNumber", roomNumber);
        params.put("building", building);
        // 数据库查询
        try {
            dbFreeBeds = bedMapper.selectFreeBedsByRoom(params);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找空闲床位数据库错误");
            throw e;
        }
        // 封装数据
        for (Map<String, Object> bed : dbFreeBeds) {
            PairVO pair = new PairVO();
            pair.setValue((String) bed.get("bed_no"));
            pair.setLabel(bed.get("bed_no") + "号床");
            data.add(pair);
        }
        // 数据包装并返回
        result.setData(data);
        result.setCode(200);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 交换客户床位
     *
     * @param params 床位交换参数
     * @return 操作结果
     */
    @Override
    public ApiResult swap(BedSwapRequest params) {
        // 修改旧床信息
        // 变量准备
        ApiResult result = new ApiResult();
        Bed oldBed, newBed;
        BedRecord oldBedRecord;
        BedRecord newBedRecord = new BedRecord();
        Map<String, Object> newBedParams = new HashMap<>();
        newBedParams.put("building", params.getNewBuildingNumber());
        newBedParams.put("roomNo", params.getNewRoomNumber());
        newBedParams.put("bedNo", params.getNewBedNumber());
        // 数据库查询
        // 获取旧床位使用记录
        QueryWrapper<BedRecord> oldBedRecordQueryWrapper = new QueryWrapper<>();
        oldBedRecordQueryWrapper.eq("bed_record_id", params.getOldBedId());
        oldBedRecordQueryWrapper.eq("history", "1");
        oldBedRecordQueryWrapper.eq("del_flag", "0");
        try {
            oldBedRecord = bedRecordMapper.selectOne(oldBedRecordQueryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询旧床位使用记录数据库错误");
            throw e;
        }
        // 修改旧床位使用记录
        oldBedRecord.setHistory("0");
        oldBedRecord.setUsageEndDate(String.valueOf(params.getOldBedEndDate()));
        try {
            bedRecordMapper.updateById(oldBedRecord);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改旧床位记录数据库错误");
            throw e;
        }
        // 根据旧床位使用记录获取旧床位信息
        try {
            oldBed = bedMapper.selectById(oldBedRecord.getBedId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改旧床位状态数据库错误");
            throw e;
        }
        // 修改旧床位信息
        oldBed.setStatus("free");
        try {
            bedMapper.updateById(oldBed);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改旧床位状态数据库错误");
            throw e;
        }
        // 获取新床位信息
        try {
            newBed = bedMapper.selectBedByBedDetails(newBedParams);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取新床位信息数据库错误");
            throw e;
        }
        // 修改新床位信息
        newBed.setStatus("used");
        try {
            bedMapper.updateById(newBed);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改新床位状态数据库错误");
            throw e;
        }
        // 新增新床位使用记录
        newBedRecord.setBedId(newBed.getBedId());
        newBedRecord.setBedNo(newBed.getBedNo());
        newBedRecord.setCustomerId(oldBedRecord.getCustomerId());
        newBedRecord.setUsageStartDate(params.getNewBedStartDate());
        newBedRecord.setUsageEndDate(params.getNewBedEndDate());
        try {
            bedRecordMapper.insert(newBedRecord);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("增加新床位记录数据库错误");
            throw e;
        }
        // 获取客户信息
        Customer dbCustomer = new Customer();
        try {
            dbCustomer = customerMapper.selectById(oldBedRecord.getCustomerId());
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("获取客户信息数据库错误");
            throw e;
        }
        // 修改客户床位id
        dbCustomer.setBedId(newBed.getBedId());
        try {
            customerMapper.updateById(dbCustomer);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("修改客户床位信息数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setMessage("修改成功");
        return result;
    }

    /**
     * 查询楼层列表
     *
     * @return 楼层列表
     */
    @Override
    public ApiResult<List<String>> floorList(String building) {
        // 获取楼层列表
        // 变量准备
        ApiResult<List<String>> result = new ApiResult<>();
        List<String> data;
        // 数据库查询
        try {
            if (building != null && !building.isEmpty()) {
                data = roomMapper.getFloorListByBuilding(building);
            } else {
                data = roomMapper.getFloorList();
            }
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询楼层列表数据库错误");
            throw e;
        }
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 统计空闲床位数量
     *
     * @return 空闲床位数量
     */
    @Override
    public ApiResult<BedStatusDistributionVO> statusDistribution() {
        ApiResult<BedStatusDistributionVO> result = new ApiResult<>();
        BedStatusDistributionVO data = new BedStatusDistributionVO();
        try {
            QueryWrapper<Bed> qw = new QueryWrapper<>();
            qw.eq("del_flag", "0");
            data.setTotal(bedMapper.selectCount(qw));
            qw.eq("status", "free");
            data.setFree(bedMapper.selectCount(qw));
            qw = new QueryWrapper<>();
            qw.eq("del_flag", "0").eq("status", "used");
            data.setUsed(bedMapper.selectCount(qw));
            qw = new QueryWrapper<>();
            qw.eq("del_flag", "0").eq("status", "out");
            data.setOut(bedMapper.selectCount(qw));
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询床位状态分布数据库错误");
            throw e;
        }
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }
}
