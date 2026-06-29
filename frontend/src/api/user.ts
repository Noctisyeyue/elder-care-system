import { get, post } from '@/utils/request'

/** 用户登录请求参数 */
export interface LoginParams {
  userName: string
  password: string
  role: string
}

/** 用户列表查询参数 */
export interface UserListParams {
  userName?: string
  pageNum: number
  pageSize: number
}

/** 用户新增/修改表单 */
export interface UserForm {
  userName: string
  password?: string
  realName: string
  phone: string
  email: string
  gender: string
  role: string
}

/** 登录响应：token */
export interface LoginResult {
  token: string
}

/**
 * 用户登录
 * @param params 登录参数（用户名、密码、角色编码）
 * @returns 登录结果，包含 token
 */
export function login(params: LoginParams) {
  return post<LoginResult>('/user/login', params)
}

/**
 * 分页查询用户列表
 * @param params 查询参数（用户名模糊、页码、每页大小）
 * @returns 用户列表分页数据
 */
export function getUserList(params: UserListParams) {
  return get('/user/list', params)
}

/**
 * 获取用户总数
 * @returns 用户数量
 */
export function getUserCount() {
  return get('/user/count')
}

/**
 * 获取各角色人数统计
 * @returns 角色人数列表
 */
export function getRoleNum() {
  return get('/user/roleNum')
}

/**
 * 新增用户
 * @param form 用户表单数据
 */
export function addUser(form: UserForm) {
  return post('/user/add', form)
}

/**
 * 修改用户
 * @param form 用户表单数据
 */
export function updateUser(form: UserForm) {
  return post('/user/update', form)
}

/**
 * 删除用户（支持批量）
 * @param userNames 用户名列表
 */
export function deleteUsers(userNames: string[]) {
  return post('/user/del', { userNames })
}

/** 上传用户头像 */
export function uploadAvatar(formData: FormData) {
  return post('/user/avatar/upload', formData)
}

/** 获取用户头像 */
export function getUserAvatar() {
  return get('/user/avatar/get')
}

/** 获取用户邮箱 */
export function getUserEmail() {
  return get('/user/email/get')
}
