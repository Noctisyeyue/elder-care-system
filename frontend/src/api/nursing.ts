import { get, post, put, del } from '@/utils/request'

/** 护理级别查询参数 */
export interface LevelListParams {
  status?: string
  pageNum: number
  pageSize: number
}

/** 护理项目查询参数 */
export interface ItemListParams {
  nursingItemName?: string
  status?: string
  pageNum: number
  pageSize: number
}

// ===== 护理级别 =====

/** 查询护理级别列表 */
export function getNursingLevelList(params: LevelListParams) {
  return get('/nursing/level/list', params)
}

/** 新增护理级别 */
export function addNursingLevel(params: Record<string, unknown>) {
  return post('/nursing/level/add', params)
}

/** 修改护理级别 */
export function updateNursingLevel(params: Record<string, unknown>) {
  return put('/nursing/level/update', params)
}

/** 查询护理级别下的护理项目 */
export function getNursingLevelItems(levelId: number) {
  return get('/nursing/level/item/list', { levelId, pageNum: 1, pageSize: 9999 })
}

/** 保存护理级别与护理项目的关联 */
export function saveNursingLevelItems(levelId: number, itemIds: number[]) {
  return post('/nursing/level/item/save', { levelId, itemIds })
}

// ===== 护理项目 =====

/** 查询护理项目列表 */
export function getNursingItemList(params: ItemListParams) {
  return get('/nursing/item/list', params)
}

/** 新增护理项目 */
export function addNursingItem(params: Record<string, unknown>) {
  return post('/nursing/item/add', params)
}

/** 修改护理项目 */
export function updateNursingItem(params: Record<string, unknown>) {
  return put('/nursing/item/update', params)
}

// ===== 护理记录 =====

/** 查询护理记录列表 */
export function getNursingRecordList(params: Record<string, unknown>) {
  return get('/nursing/record/list', params)
}

/** 删除护理记录 */
export function removeNursingRecord(id: number) {
  return post('/nursing/record/remove', { id })
}

// ===== 客户护理 =====

/** 查询客户的护理项目 */
export function getCustomerNursingItems(customerId: number) {
  return get('/nursing/customer/items', { customerId })
}

/** 为客户添加护理级别 */
export function addCustomerNursingLevel(customerId: number, levelId: number) {
  return post('/nursing/customer/add', { customerId, levelId })
}

/** 移除客户的护理级别 */
export function removeCustomerNursingLevel(customerId: number, levelId: number) {
  return post('/nursing/customer/remove', { customerId, levelId })
}

/** 删除护理项目 */
export function deleteNursingItem(id: number) {
  return del(`/nursing/item/delete/${id}`)
}
