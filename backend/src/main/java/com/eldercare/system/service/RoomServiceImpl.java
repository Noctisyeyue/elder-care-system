package com.eldercare.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eldercare.system.entity.Bed;
import com.eldercare.system.entity.Room;
import com.eldercare.system.mapper.BedMapper;
import com.eldercare.system.mapper.RoomMapper;
import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.room.RoomOption;
import com.eldercare.system.po.room.RoomResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 房间服务实现 */
@Service
public class RoomServiceImpl implements RoomService{
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private BedMapper bedMapper;
    @Override
    public ApiResult<List<RoomResult>> options() {
        //查询数据库中的所有房间信息
        List<Room> rooms = roomMapper.selectList(null);
        //封装返回结果
        List<RoomOption> roomOptions = new ArrayList<>();
        for(Room room : rooms){
            RoomOption roomOption = new RoomOption();
            roomOption.setValue(room.getRoomNo());
            roomOption.setLabel("房间 "+room.getRoomNo());
            roomOptions.add(roomOption);
        }
        //封装返回结果
        ApiResult<List<RoomResult> > result = new ApiResult<>();
        RoomResult roomResult = new RoomResult("606楼",roomOptions);
        result.setCode(200);
        result.setMessage("success");
        result.setData(List.of(roomResult));
        return result;
    }

    @Override
    public ApiResult updateBedStatus(Long roomNumber, Long bedNumber) {
        ApiResult result = new ApiResult<>();
        //根据床号和房间号获取床id
        Map<String, Object> newBedParams = new HashMap<>();
        newBedParams.put("bedNo", bedNumber);
        newBedParams.put("roomNo", roomNumber);
        try {
            Long bedId = bedMapper.selectBedByBedDetails(newBedParams).getBedId();
            // 根据床id将status变为free
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
