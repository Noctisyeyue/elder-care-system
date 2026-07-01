import { get, post, put } from '@/utils/request'

/**
 * 查询菜品列表。
 *
 * @param params 菜品查询参数
 * @returns 菜品分页列表
 */
export function getFoodList(params: Record<string, unknown>) {
  return get('/diet/foodList', params)
}

/**
 * 新增菜品。
 *
 * @param params 菜品保存参数
 * @returns 新增处理结果
 */
export function addFood(params: Record<string, unknown>) {
  return post('/diet/addFood', params)
}

/**
 * 修改菜品。
 *
 * @param params 菜品更新参数
 * @returns 更新处理结果
 */
export function updateFood(params: Record<string, unknown>) {
  return put('/diet/updateFood', params)
}

/**
 * 获取可选菜品。
 *
 * @param time 餐次编码
 * @param pork 清真标识
 * @returns 可选菜品列表
 */
export function getAvailableDishes(time: string, pork: string) {
  return get('/diet/availableDishes', { time, pork })
}

/**
 * 查询套餐列表。
 *
 * @param params 套餐查询参数
 * @returns 套餐分页列表
 */
export function getSetMealList(params: Record<string, unknown>) {
  return get('/diet/setMeal/list', params)
}

/**
 * 修改套餐。
 *
 * @param params 套餐保存参数
 * @returns 保存处理结果
 */
export function updateSetMeal(params: Record<string, unknown>) {
  return post('/diet/setMeal/update', params)
}

/**
 * 删除套餐。
 *
 * @param setMealId 套餐ID
 * @returns 删除处理结果
 */
export function removeSetMeal(setMealId: number | string) {
  return post('/diet/setMeal/remove', { setMealId })
}

/**
 * 保存套餐菜品关联。
 *
 * @param params 套餐菜品保存参数
 * @returns 保存处理结果
 */
export function saveSetMealDishes(params: Record<string, unknown>) {
  return post('/diet/saveSetMealDishes', params)
}

/**
 * 查询指定日期的膳食记录。
 *
 * @param date 日期
 * @returns 当日套餐列表
 */
export function getDailySetMealList(date: string) {
  return get('/diet/setMeal/dailyList', { date })
}

/**
 * 保存每日膳食记录。
 *
 * @param params 每日膳食保存参数
 * @returns 保存处理结果
 */
export function saveDishes(params: Record<string, unknown>) {
  return post('/diet/saveDishes', params)
}

/**
 * 为客户分配膳食套餐。
 *
 * @param params 客户套餐分配参数
 * @returns 保存处理结果
 */
export function saveCustomerSetMeal(params: Record<string, unknown>) {
  return post('/diet/saveCustomerSetMeal', params)
}

/**
 * 查询膳食模块客户列表。
 *
 * @param params 客户查询参数
 * @returns 客户膳食分页列表
 */
export function getDietCustomerList(params: Record<string, unknown>) {
  return get('/diet/customerList', params)
}

/**
 * 获取所有可选菜品。
 *
 * @returns 可选菜品列表
 */
export function getDietAvailableDishes() {
  return get('/diet/availableDishes')
}

/**
 * 查询指定月份的膳食日历。
 *
 * @param year 年份
 * @param month 月份
 * @returns 月度膳食日历
 */
export function getDietMonthList(year: number, month: number) {
  return get('/diet/monthList', { year, month })
}

/**
 * 保存膳食日历配置。
 *
 * @param params 膳食日历保存参数
 * @returns 保存处理结果
 */
export function saveDietCalendar(params: Record<string, unknown>) {
  return post('/diet/saveDietCalendar', params)
}

/**
 * 上传菜品图片。
 *
 * @param formData 图片表单数据
 * @returns 上传处理结果
 */
export function uploadDishImage(formData: FormData) {
  return post('/diet/img/upload', formData)
}

/** 本周膳食配餐量统计 */
export function getWeeklyMealCount() {
  return get('/diet/weeklyMealCount')
}
