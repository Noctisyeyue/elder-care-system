package com.eldercare.system.service;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.room.RoomResult;

import java.util.List;

/** 房间服务接口 */
public interface RoomService {
    ApiResult<List<RoomResult>> options();

    ApiResult updateBedStatus(Long roomNumber, Long bedNumber);
}
