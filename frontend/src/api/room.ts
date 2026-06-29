import { get, put } from '@/utils/request'

/**
 * 获取房间下拉选项（用于客户登记时选择房间）
 * @returns 房间选项列表
 */
export function getRoomOptions() {
  return get('/room/options')
}

/**
 * 获取指定房间的可选床位
 * @param roomNumber 房间号
 * @returns 床位列表
 */
export function getRoomBeds(roomNumber: string) {
  return get(`/room/${roomNumber}/beds`)
}

/**
 * 更新床位状态
 * @param roomNumber 房间号
 * @param bedNumber 床位号
 * @param status 床位状态
 */
export function updateBedStatus(roomNumber: string, bedNumber: string, status: string) {
  return put(`/room/${roomNumber}/bed/${bedNumber}/status`, { status })
}
