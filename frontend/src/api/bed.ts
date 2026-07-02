import { get, put, post } from '@/utils/request'
import { sortFloorList } from '@/utils/floor'

/**
 * 获取床位示意图数据（按楼层/房间/床位展示）
 * @param params 查询参数
 * @returns 床位分布数据
 */
export function getBedMap(params?: Record<string, unknown>) {
  return get('/bed/map', params)
}

/**
 * 获取楼层列表。
 *
 * @param building 楼号
 * @returns 按楼层顺序排列的楼层列表
 */
export function getFloorList(building?: string) {
  return get<string[]>('/bed/floorList', building ? { building } : undefined).then(
    (list) => sortFloorList(list || []),
  )
}

/**
 * 分页查询床位列表
 * @param params 查询参数
 * @returns 床位分页数据
 */
export function getBedList(params: Record<string, unknown>) {
  return get('/bed/list', params)
}

/**
 * 更新床位使用结束日期
 * @param id 床位记录ID
 * @param usageEndDate 使用结束日期
 */
export function updateBedUsageEndDate(id: number | string, usageEndDate: string) {
  return put(`/bed/update/${id}`, { usageEndDate })
}

/**
 * 获取有空闲床位的房间列表
 * @param building 楼号
 * @returns 空闲房间列表
 */
export function getFreeRooms(building: string) {
  return get('/bed/freeRooms', { building })
}

/**
 * 获取指定房间的空闲床位
 * @param roomNumber 房间号
 * @param building 楼号
 * @returns 空闲床位列表
 */
export function getFreeBeds(roomNumber: string, building: string) {
  return get(`/bed/freeBeds/${roomNumber}`, { building })
}

/**
 * 床位调换
 * @param swapData 调换数据
 */
export function swapBeds(swapData: Record<string, unknown>) {
  return post('/bed/swap', swapData)
}

/** 床位状态分布统计 */
export function getBedStatusDistribution() {
  return get('/bed/statusDistribution')
}
