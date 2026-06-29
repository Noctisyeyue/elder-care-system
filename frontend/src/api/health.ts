import { get, post } from '@/utils/request'

/** 查询未分配健康管家的客户 */
export function getCustomersNoCaregiver(params: Record<string, unknown>) {
  return get('/customer/noCaregiver', params)
}

/** 查询健康管家已分配的客户 */
export function getCaregiverCustomers(params: Record<string, unknown>) {
  return get('/caregiver/customers', params)
}

/** 为健康管家分配客户 */
export function setCaregiverCustomers(caregiverId: number, customerIds: number[]) {
  return post('/caregiver/setCustomers', { caregiverId, customerIds })
}

/** 查询健康管家列表 */
export function getCaregiverList(params: Record<string, unknown>) {
  return get('/caregiver/list', params)
}

/** 查询已购买的护理项目 */
export function getPurchasedItems(params: Record<string, unknown>) {
  return get('/customer/purchasedItems', params)
}

/** 续费护理项目 */
export function renewNursingItem(params: Record<string, unknown>) {
  return post('/nursing/renew', params)
}

/** 查询客户护理级别对应的项目 */
export function getNursingCustomerLevelItems(params: Record<string, unknown>) {
  return get('/nursing/customerLevel/item/list', params)
}

/** 检查客户是否已购买某护理项目 */
export function checkIsPurchased(params: Record<string, unknown>) {
  return get('/customer/isPurchased', params)
}

/** 为客户购买护理项目 */
export function buyNursingItem(params: Record<string, unknown>) {
  return post('/customer/buyItem', params)
}

/** 添加护理记录 */
export function addNursingRecord(params: Record<string, unknown>) {
  return post('/nursing/record/add', params)
}

/** 健康管家首页统计 */
export function getCaregiverHomeStats() {
  return get('/caregiver/homeStats')
}
