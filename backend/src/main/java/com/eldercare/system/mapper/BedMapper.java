package com.eldercare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.system.entity.Bed;

import java.util.List;
import java.util.Map;

/** BedMapper */
public interface BedMapper extends BaseMapper<Bed> {

    List<Map<String, Object>> selectRoomsByFloor(String floor);

    List<Map<String, Object>> selectBedsByRoom(Object roomId);

    List<Map<String, Object>> selectBedsByCustomerNameAndCheckInDateAndHistory(Map<String, Object> paramsMap);

    List<Map<String, Object>> selectBedsByCustomerNameAndHistory(Map<String, Object> paramsMap);

    List<Map<String, Object>> selectFreeRooms();

    List<Map<String, Object>> selectFreeBedsByRoom(String roomNumber);

    Bed selectBedByBedDetails(Map<String, Object> newBedParams);

    void updateBedStatus(Long bedId);

    void updateBedStatusOut(Long bedId);

    void updateBedStatusUsed(Long bedId);

}
