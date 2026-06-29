package com.eldercare.system.service;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.room.RoomResult;

import java.util.List;

/** 房间服务接口 */
public interface RoomService {

    /**
     * 查询房间选项列表
     *
     * @return 房间选项列表
     */
    ApiResult<List<RoomResult>> options();

    /**
     * 更新指定床位状态
     *
     * @param roomNumber 房间号
     * @param bedNumber  床位号
     * @return 操作结果
     */
    ApiResult updateBedStatus(Long roomNumber, Long bedNumber);
}
