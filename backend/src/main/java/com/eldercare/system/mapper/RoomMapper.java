package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.Room;

import java.util.List;

/** RoomMapper */
public interface RoomMapper extends BaseMapper<Room> {
    List<String> getFloorList();

    List<String> getFloorListByBuilding(String building);
}
