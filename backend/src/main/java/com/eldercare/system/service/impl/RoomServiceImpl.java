package com.eldercare.system.service.impl;
import com.eldercare.system.service.RoomService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.entity.Bed;
import com.eldercare.system.entity.Room;
import com.eldercare.system.mapper.BedMapper;
import com.eldercare.system.mapper.RoomMapper;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.vo.room.RoomOptionVO;
import com.eldercare.system.vo.room.RoomResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间服务实现
 */
@Service
public class RoomServiceImpl implements RoomService{

    /** 房间 Mapper */
    @Autowired
    private RoomMapper roomMapper;

    /** 床位 Mapper */
    @Autowired
    private BedMapper bedMapper;

    /**
     * 查询房间选项列表
     *
     * @param building 楼号
     * @return 房间选项列表
     */
    @Override
    public ApiResult<List<RoomResultVO>> options(String building) {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        if (building != null && !building.isEmpty()) {
            wrapper.eq("building", building);
        }
        wrapper.orderByAsc("floor", "room_no");
        List<Room> rooms = roomMapper.selectList(wrapper);

        Map<String, List<RoomOptionVO>> floorGroups = new LinkedHashMap<>();
        for (Room room : rooms) {
            RoomOptionVO roomOption = new RoomOptionVO();
            roomOption.setValue(room.getRoomNo());
            roomOption.setLabel("房间 " + room.getRoomNo());
            floorGroups.computeIfAbsent(room.getFloor(), k -> new ArrayList<>()).add(roomOption);
        }

        List<RoomResultVO> data = new ArrayList<>();
        for (Map.Entry<String, List<RoomOptionVO>> entry : floorGroups.entrySet()) {
            data.add(new RoomResultVO(entry.getKey(), entry.getValue()));
        }

        ApiResult<List<RoomResultVO>> result = new ApiResult<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 更新指定床位状态为空闲
     *
     * @param building   楼号
     * @param roomNumber 房间号
     * @param bedNumber  床位号
     * @return 操作结果
     */
    @Override
    public ApiResult updateBedStatus(String building, Long roomNumber, Long bedNumber) {
        ApiResult result = new ApiResult<>();
        Map<String, Object> newBedParams = new HashMap<>();
        newBedParams.put("bedNo", String.valueOf(bedNumber));
        newBedParams.put("roomNo", String.valueOf(roomNumber));
        newBedParams.put("building", building);
        try {
            Long bedId = bedMapper.selectBedByBedDetails(newBedParams).getBedId();
            bedMapper.updateBedStatus(bedId);
            result.setCode(200);
            result.setMessage("释放床位成功");
        }catch (Exception e){
            result.setCode(500);
            result.setMessage("更新床状态失败，数据库错误");
            return result;
        }
        return result;
    }
}
