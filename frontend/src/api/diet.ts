import { get, post, put } from '@/utils/request'

// ===== 菜品管理 =====

/** 查询菜品列表 */
export function getFoodList(params: Record<string, unknown>) {
  return get('/diet/foodList', params)
}

/** 新增菜品 */
export function addFood(params: Record<string, unknown>) {
  return post('/diet/addFood', params)
}

/** 修改菜品 */
export function updateFood(params: Record<string, unknown>) {
  return put('/diet/updateFood', params)
}

/** 获取可选菜品（按时间和猪肉过滤） */
export function getAvailableDishes(time: string, pork: string) {
  return get('/diet/availableDishes', { time, pork })
}

// ===== 套餐管理 =====

/** 查询套餐列表 */
export function getSetMealList(params: Record<string, unknown>) {
  return get('/diet/setMeal/list', params)
}

/** 修改套餐 */
export function updateSetMeal(params: Record<string, unknown>) {
  return post('/diet/setMeal/update', params)
}

/** 删除套餐 */
export function removeSetMeal(setMealId: number | string) {
  return post('/diet/setMeal/remove', { setMealId })
}

/** 保存套餐菜品关联 */
export function saveSetMealDishes(params: Record<string, unknown>) {
  return post('/diet/saveSetMealDishes', params)
}

// ===== 膳食日历 =====

/** 查询指定日期的膳食记录 */
export function getDailySetMealList(date: string) {
  return get('/diet/setMeal/dailyList', { date })
}

/** 保存每日膳食记录 */
export function saveDishes(params: Record<string, unknown>) {
  return post('/diet/saveDishes', params)
}

/** 为客户分配膳食套餐 */
export function saveCustomerSetMeal(params: Record<string, unknown>) {
  return post('/diet/saveCustomerSetMeal', params)
}

/** 查询客户列表（膳食模块专用） */
export function getDietCustomerList(params: Record<string, unknown>) {
  return get('/diet/customerList', params)
}

/** 获取所有可选菜品（无参数） */
export function getDietAvailableDishes() {
  return get('/diet/availableDishes')
}

/** 查询指定月份的膳食日历 */
export function getDietMonthList(year: number, month: number) {
  return get('/diet/monthList', { year, month })
}

/** 保存膳食日历配置 */
export function saveDietCalendar(params: Record<string, unknown>) {
  return post('/diet/saveDietCalendar', params)
}

/** 上传菜品图片 */
export function uploadDishImage(formData: FormData) {
  return post('/diet/img/upload', formData)
}
