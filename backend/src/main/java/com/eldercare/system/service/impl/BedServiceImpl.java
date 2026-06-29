
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
import com.eldercare.system.po.bed.params.ListParams;
import com.eldercare.system.po.bed.params.SwapParams;
import com.eldercare.system.po.bed.result.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BedServiceImpl implements BedService{
    @Autowired
    private BedMapper bedMapper;
    @Autowired
    private BedRecordMapper bedRecordMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public ApiResult<List<MapResult>> getMap(String floor) {
        // 按层获取床位示意图
        // 变量准备
        ApiResult<List<MapResult>> result = new ApiResult<>();
        List<MapResult> list = new ArrayList<>();
        List<Map<String, Object>> dbRooms;
        // 数据库查询
        // 按楼层查询房间
        try{
            dbRooms = bedMapper.selectRoomsByFloor(floor);
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
            List<BedResult> details = new ArrayList<>();
            List<Map<String, Object>> dbBeds;
            try {
                dbBeds = bedMapper.selectBedsByRoom(room.get("room_id"));
            } catch (Exception e) {
                result.setCode(500);
                result.setMessage("按房间查找床数据库错误");
                throw e;
            }
            // 包装单个房间数据
            MapResult mapResult = new MapResult();
            mapResult.setRoomNumber(String.valueOf(room.get("room_no")));
            List<Bed> bedList = new ArrayList<>();
            for(Map bed : dbBeds){
                Bed bedData = new Bed();
                bedData.setBedNo(String.valueOf(bed.get("bed_no")));
                bedData.setStatus(String.valueOf(bed.get("status")));
                bedList.add(bedData);
                BedResult detail = new BedResult();
                Customer customer;
                QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("bed_id", bed.get("bed_id"));
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

    @Override
    public ApiResult<ListResult> getList(ListParams params) {
        // 按条件获取床位使用信息列表
        // 变量准备
        ApiResult<ListResult> result = new ApiResult<>();
        ListResult data = new ListResult();
        List<BedResult> list = new ArrayList<>();
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
                BedResult bedResult = new BedResult();
                bedResult.setId((Long) bed.get("bed_record_id"));
                bedResult.setCustomerName(String.valueOf(bed.get("customer_name")));
                bedResult.setGender(String.valueOf(bed.get("gender")));
                bedResult.setUsageStartDate(String.valueOf(bed.get("usage_start_date")));
                bedResult.setUsageEndDate(String.valueOf(bed.get("usage_end_date")));
                bedResult.setHistory(String.valueOf(bed.get("history")));
                bedResult.setStatus(String.valueOf(bed.get("status")));
                // 拼装床位详情
                String building, roomNo, bedNo, bedDetails;
                building  = String.valueOf(bed.get("building"));
                roomNo = String.valueOf(bed.get("room_no"));
                bedNo = String.valueOf(bed.get("bed_no"));
                bedDetails = building + "#" + roomNo + "-" + bedNo;
                bedResult.setBedDetails(bedDetails);
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
                    BedResult bedResult = new BedResult();
                    bedResult.setId((Long) bed.get("bed_record_id"));
                    bedResult.setCustomerName(String.valueOf(bed.get("customer_name")));
                    bedResult.setGender(String.valueOf(bed.get("gender")));
                    bedResult.setUsageStartDate(String.valueOf(bed.get("usage_start_date")));
                    bedResult.setUsageEndDate(String.valueOf(bed.get("usage_end_date")));
                    bedResult.setHistory(String.valueOf(bed.get("history")));
                    bedResult.setStatus(String.valueOf(bed.get("status")));
                    // 拼装床位详情
                    String building, roomNo, bedNo, bedDetails;
                    building  = String.valueOf(bed.get("building"));
                    roomNo = String.valueOf(bed.get("room_no"));
                    bedNo = String.valueOf(bed.get("bed_no"));
                    bedDetails = building + "#" + roomNo + "-" + bedNo;
                    bedResult.setBedDetails(bedDetails);
                    list.add(bedResult);
                }
            }
        }
        // 分页
        int totalItems = list.size();
        int fromIndex = (params.getPageNum() - 1) * params.getPageSize();
        int toIndex = Math.min(fromIndex + params.getPageSize(), totalItems);
        List<BedResult> page = list.subList(fromIndex,toIndex);
        // 数据包装并返回
        data.setList(page);
        data.setTotal(totalItems);
        result.setData(data);
        result.setCode(200);
        result.setMessage("查询成功");
        return result;
    }

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

    @Override
    public ApiResult<List<FreeRoomsResult>> selectFreeRooms() {
        // 获取有空闲床位的房间
        // 变量准备
        ApiResult<List<FreeRoomsResult>> result = new ApiResult<>();// 返回结果
        List<FreeRoomsResult> data = new ArrayList<>();
        List<String> floorList;
        List<Map<String, Object>> dbFreeRooms;// 数据库查询结果
        // 数据库查询
        // 获取楼层列表
        try {
            floorList = roomMapper.getFloorList();
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找楼层列表数据库错误");
            throw e;
        }
        for (String floor : floorList){
            FreeRoomsResult freeRoomsResult = new FreeRoomsResult();
            freeRoomsResult.setLabel(floor);
            freeRoomsResult.setOptions(new ArrayList<>());
            data.add(freeRoomsResult);
        }
        // 获取有空闲床位的房间
        try {
            dbFreeRooms = bedMapper.selectFreeRooms();
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找有空闲床位房间数据库错误");
            throw e;
        }
        // 将dbFreeRooms拆分并包装成前端所需数据格式
        for (Map<String, Object> room : dbFreeRooms) {
            for(String floor : floorList){
                if (room.get("floor").equals(floor)){
                    for(FreeRoomsResult freeRoomsResult : data){
                        if (freeRoomsResult.getLabel().equals(floor)){
                            Pair pair = new Pair();
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

    @Override
    public ApiResult<List<Pair>> selectFreeBeds(String roomNumber) {
        // 获取指定房间的空闲床位
        // 变量准备
        ApiResult<List<Pair>> result = new ApiResult<>();
        List<Pair> data = new ArrayList<>();
        List<Map<String, Object>> dbFreeBeds;
        // 数据库查询
        try {
            dbFreeBeds = bedMapper.selectFreeBedsByRoom(roomNumber);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查找空闲床位数据库错误");
            throw e;
        }
        // 封装数据
        for (Map<String, Object> bed : dbFreeBeds) {
            Pair pair = new Pair();
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

    @Override
    public ApiResult swap(SwapParams params) {
        // 修改旧床信息
        // 变量准备
        ApiResult result = new ApiResult();
        Bed oldBed, newBed;
        BedRecord oldBedRecord;
        BedRecord newBedRecord = new BedRecord();
        Map<String, Object> newBedParams = new HashMap<>();
        newBedParams.put("roomNo", params.getNewBedDetails().split("#")[1].split("-")[0]);
        newBedParams.put("bedNo", params.getNewBedDetails().split("-")[1]);
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

    @Override
    public ApiResult<List<String>> floorList() {
        // 获取楼层列表
        // 变量准备
        ApiResult<List<String>> result = new ApiResult<>();
        List<String> data;
        // 数据库查询
        try {
            data = roomMapper.getFloorList();
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

    @Override
    public ApiResult<Long> freeBedCount() {
        // 获取空床数量
        // 变量准备
        ApiResult<Long> result = new ApiResult<>();
        Long data;
        // 数据库查询
        QueryWrapper<Bed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "free");
        queryWrapper.eq("del_flag", "0");
        try {
            data = bedMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询空床数量数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }

    @Override
    public ApiResult<Long> BedCount() {
        // 获取总床数量
        // 变量准备
        ApiResult<Long> result = new ApiResult<>();
        Long data;
        // 数据库查询
        QueryWrapper<Bed> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", "0");
        try {
            data = bedMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            result.setCode(500);
            result.setMessage("查询总床数量数据库错误");
            throw e;
        }
        // 数据包装并返回
        result.setCode(200);
        result.setData(data);
        result.setMessage("查询成功");
        return result;
    }
}
