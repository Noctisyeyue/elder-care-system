import { get, post } from '@/utils/request'

/**
 * 查询未分配健康管家的客户。
 *
 * @param params 客户查询参数
 * @returns 未分配健康管家的客户列表
 */
export function getCustomersNoCaregiver(params: Record<string, unknown>) {
  return get('/customer/noCaregiver', params)
}

/**
 * 查询健康管家已分配的客户。
 *
 * @param params 健康管家客户查询参数
 * @returns 健康管家客户列表
 */
export function getCaregiverCustomers(params: Record<string, unknown>) {
  return get('/caregiver/customers', params)
}

/**
 * 为健康管家分配客户。
 *
 * @param params 包含 caregiverId, customerIds 的对象
 * @returns 分配处理结果
 */
export function setCaregiverCustomers(params: Record<string, unknown>) {
  return post('/caregiver/setCustomers', params)
}

/**
 * 查询健康管家列表。
 *
 * @param params 健康管家查询参数
 * @returns 健康管家分页列表
 */
export function getCaregiverList(params: Record<string, unknown>) {
  return get('/caregiver/list', params)
}

/**
 * 查询已购买的护理项目。
 *
 * @param params 已购护理项目查询参数
 * @returns 已购护理项目列表
 */
export function getPurchasedItems(params: Record<string, unknown>) {
  return get('/customer/purchasedItems', params)
}

/**
 * 续费护理项目。
 *
 * @param params 护理项目续费参数
 * @returns 续费处理结果
 */
export function renewNursingItem(params: Record<string, unknown>) {
  return post('/nursing/renew', params)
}

/**
 * 查询客户护理级别对应的项目。
 *
 * @param params 客户护理项目查询参数
 * @returns 护理项目分页列表
 */
export function getNursingCustomerLevelItems(params: Record<string, unknown>) {
  return get('/nursing/customerLevel/item/list', params)
}

/**
 * 检查客户是否已购买某护理项目。
 *
 * @param params 购买状态查询参数
 * @returns 是否已购买
 */
export function checkIsPurchased(params: Record<string, unknown>) {
  return get('/customer/isPurchased', params)
}

/**
 * 为客户购买护理项目。
 *
 * @param params 护理项目购买参数
 * @returns 购买处理结果
 */
export function buyNursingItem(params: Record<string, unknown>) {
  return post('/customer/buyItem', params)
}

/**
 * 添加护理记录。
 *
 * @param params 护理记录保存参数
 * @returns 保存处理结果
 */
export function addNursingRecord(params: Record<string, unknown>) {
  return post('/nursing/record/add', params)
}

/**
 * 查询健康管家首页统计。
 *
 * @returns 首页统计数据
 */
export function getCaregiverHomeStats() {
  return get('/caregiver/homeStats')
}
