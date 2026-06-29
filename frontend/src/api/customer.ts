import { get, post, put, del } from '@/utils/request'

/** 客户列表查询参数 */
export interface CustomerListParams {
  customerName?: string
  pageNum?: number
  pageSize?: number
}

/** 客户入住登记表单 */
export interface CustomerRegisterForm {
  [key: string]: unknown
}

/** 外出/退住申请参数 */
export interface ApplyParams {
  [key: string]: unknown
}

/**
 * 分页查询客户列表
 * @param params 查询参数（客户名、页码、每页大小）
 * @returns 客户列表分页数据
 */
export function getCustomerList(params: CustomerListParams) {
  return get('/customer/list', params)
}

/**
 * 查询当前健康管家服务的客户
 * @param params 查询参数
 * @returns 客户列表
 */
export function getMyCustomers(params?: Record<string, unknown>) {
  return get('/customer/myCustomers', params)
}

/**
 * 客户入住登记
 * @param form 客户登记表单
 */
export function registerCustomer(form: CustomerRegisterForm) {
  return post('/customer/register', form)
}

/**
 * 查询外出申请列表
 * @param params 查询参数
 * @returns 外出申请列表
 */
export function getOutingList(params: CustomerListParams) {
  return get('/customer/outing/list', params)
}

/**
 * 查询当前用户的外出申请
 * @param params 查询参数
 * @returns 我的外出申请列表
 */
export function getMyOutingApplications(params: CustomerListParams) {
  return get('/customer/outing/myApplications', params)
}

/**
 * 提交外出申请
 * @param params 外出申请参数
 */
export function applyOuting(params: ApplyParams) {
  return post('/customer/outing/apply', params)
}

/**
 * 查询退住申请列表
 * @param params 查询参数
 * @returns 退住申请列表
 */
export function getCheckOutList(params: CustomerListParams) {
  return get('/customer/checkout/list', params)
}

/**
 * 查询当前用户的退住申请
 * @param params 查询参数
 * @returns 我的退住申请列表
 */
export function getMyCheckOutApplications(params: CustomerListParams) {
  return get('/customer/checkout/myApplications', params)
}

/**
 * 提交退住申请
 * @param params 退住申请参数
 */
export function applyCheckOut(params: ApplyParams) {
  return post('/customer/checkout/apply', params)
}

/**
 * 删除客户
 * @param id 客户ID
 */
export function deleteCustomer(id: number | string) {
  return del(`/customer/${id}`)
}

/**
 * 更新客户信息
 * @param id 客户ID
 * @param params 更新数据
 */
export function updateCustomer(id: number | string, params: Record<string, unknown>) {
  return put(`/customer/update/${id}`, params)
}

/**
 * 撤销外出申请
 * @param id 外出申请ID
 */
export function cancelOuting(id: number | string) {
  return post(`/customer/outing/cancel/${id}`)
}

/**
 * 撤销退住申请
 * @param id 退住申请ID
 */
export function cancelCheckOut(id: number | string) {
  return post(`/customer/checkout/cancel/${id}`)
}

/**
 * 外出归来登记
 * @param id 外出记录ID
 * @param params 归来信息
 */
export function returnOuting(id: number | string, params: Record<string, unknown>) {
  return put(`/customer/outing/return/${id}`, params)
}

/**
 * 审批外出申请
 * @param id 外出申请ID
 * @param params 审批参数
 */
export function approveOuting(id: number | string, params: Record<string, unknown>) {
  return post(`/customer/outing/approve/${id}`, params)
}

/**
 * 审批退住申请
 * @param id 退住申请ID
 * @param params 审批参数
 */
export function approveCheckOut(id: number | string, params: Record<string, unknown>) {
  return post(`/customer/checkout/approve/${id}`, params)
}

/**
 * 释放客户床位
 * @param customerId 客户ID
 * @param bedNumber 床位号
 */
export function releaseCustomerBed(customerId: number | string, bedNumber: string) {
  return del(`/customer/${customerId}/bed/${bedNumber}`)
}
