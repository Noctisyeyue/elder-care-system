package com.eldercare.system.service;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.bed.params.ListParams;
import com.eldercare.system.po.bed.params.SwapParams;
import com.eldercare.system.po.bed.result.*;

import java.util.List;
import java.util.Map;

/** 床位服务接口 */
public interface BedService {
    ApiResult<List<MapResult>> getMap(String floor);

    ApiResult<ListResult> getList(ListParams params);

    ApiResult updateUsageEndDateById(Long id, Map<String,String> params);

    ApiResult<List<FreeRoomsResult>> selectFreeRooms();

    ApiResult<List<Pair>> selectFreeBeds(String roomNumber);

    ApiResult swap(SwapParams params);

    ApiResult<List<String>> floorList();

    ApiResult<Long> freeBedCount();

    ApiResult<Long> BedCount();
}
