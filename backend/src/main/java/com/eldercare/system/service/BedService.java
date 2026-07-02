package com.eldercare.system.service;

import com.eldercare.system.util.ApiResult;
import com.eldercare.system.dto.bed.BedListRequest;
import com.eldercare.system.dto.bed.BedSwapRequest;
import com.eldercare.system.vo.bed.*;

import java.util.List;
import java.util.Map;

/** 床位服务接口 */
public interface BedService {

    /**
     * 查询床位示意图数据
     *
     * @param floor    楼层
     * @param building 楼号
     * @return 床位示意图数据
     */
    ApiResult<List<BedMapVO>> getMap(String floor, String building);

    /**
     * 分页查询床位列表
     *
     * @param params 查询参数
     * @return 床位列表
     */
    ApiResult<BedListVO> getList(BedListRequest params);

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
     * @param building 楼号
     * @return 空闲房间列表
     */
    ApiResult<List<FreeRoomVO>> selectFreeRooms(String building);

    /**
     * 查询指定房间的空闲床位
     *
     * @param roomNumber 房间号
     * @param building   楼号
     * @return 空闲床位列表
     */
    ApiResult<List<PairVO>> selectFreeBeds(String roomNumber, String building);

    /**
     * 交换客户床位
     *
     * @param params 床位交换参数
     * @return 操作结果
     */
    ApiResult swap(BedSwapRequest params);

    /**
     * 查询楼层列表
     *
     * @param building 楼号
     * @return 楼层列表
     */
    ApiResult<List<String>> floorList(String building);

    /**
     * 获取床位状态分布（空闲/占用/外出/总数）
     *
     * @return 床位状态分布数据
     */
    ApiResult<BedStatusDistributionVO> statusDistribution();
}
