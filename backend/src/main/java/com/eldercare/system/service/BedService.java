package com.eldercare.system.service;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.po.bed.params.ListParams;
import com.eldercare.system.po.bed.params.SwapParams;
import com.eldercare.system.po.bed.result.*;

import java.util.List;
import java.util.Map;

/** 床位服务接口 */
public interface BedService {

    /**
     * 查询床位示意图数据
     *
     * @param floor 楼层
     * @return 床位示意图数据
     */
    ApiResult<List<MapResult>> getMap(String floor);

    /**
     * 分页查询床位列表
     *
     * @param params 查询参数
     * @return 床位列表
     */
    ApiResult<ListResult> getList(ListParams params);

    /**
     * 修改床位使用结束日期
     *
     * @param id     床位记录ID
     * @param params 修改参数
     * @return 操作结果
     */
    ApiResult updateUsageEndDateById(Long id, Map<String,String> params);

    /**
     * 查询有空闲床位的房间
     *
     * @return 空闲房间列表
     */
    ApiResult<List<FreeRoomsResult>> selectFreeRooms();

    /**
     * 查询指定房间的空闲床位
     *
     * @param roomNumber 房间号
     * @return 空闲床位列表
     */
    ApiResult<List<Pair>> selectFreeBeds(String roomNumber);

    /**
     * 交换客户床位
     *
     * @param params 床位交换参数
     * @return 操作结果
     */
    ApiResult swap(SwapParams params);

    /**
     * 查询楼层列表
     *
     * @return 楼层列表
     */
    ApiResult<List<String>> floorList();

    /**
     * 统计空闲床位数量
     *
     * @return 空闲床位数量
     */
    ApiResult<Long> freeBedCount();

    /**
     * 统计床位总数
     *
     * @return 床位总数
     */
    ApiResult<Long> BedCount();
}
