import { get, post } from '@/utils/request'

/** 用户登录请求参数 */
export interface LoginParams {
  account: string
  password: string
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

/** 护工注册请求参数 */
export interface RegisterParams {
  userName: string
  email: string
  code: string
  password: string
  realName: string
  phone: string
  gender: string
}

/** 发送注册验证码请求参数 */
export interface SendRegisterCodeParams {
  email: string
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
 * 发送护工注册验证码（邮箱不存在才发送，60 秒防刷）
 * @param params 邮箱参数
 */
export function sendRegisterCode(params: SendRegisterCodeParams) {
  return post('/common/code/register', params)
}

/**
 * 护工注册（注册后为待审核状态）
 * @param params 注册参数
 */
export function register(params: RegisterParams) {
  return post('/user/register', params)
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

/**
 * 审核护工（将 status 从 0 改为 1）
 * @param userId 用户 ID
 */
export function auditUser(userId: number) {
  return post('/user/audit', null, { params: { userId } })
}

/**
 * 启用账号（将 status 从 2 改回 1）
 * @param userId 用户 ID
 */
export function enableUser(userId: number) {
  return post('/user/enable', null, { params: { userId } })
}

/**
 * 禁用账号（将 status 改为 2）
 * @param userId 用户 ID
 */
export function disableUser(userId: number) {
  return post('/user/disable', null, { params: { userId } })
}

/**
 * 重置密码为默认密码（123456）
 * @param userId 用户 ID
 */
export function resetUserPwd(userId: number) {
  return post('/user/resetPwd', null, { params: { userId } })
}

/** 获取用户头像 */
export function getUserAvatar() {
  return get('/user/avatar/get')
}

/** 获取用户邮箱 */
export function getUserEmail() {
  return get('/user/email/get')
}

/** 获取当前用户在数据库中的最新状态（待审核护工刷新用） */
export function getUserStatus() {
  return get<number>('/user/status/get')
}

/** 用户资料 */
export interface UserProfile {
  userName: string
  realName: string
  phone: string
  email: string
  gender: string
  roleName: string
}

/** 资料更新表单（邮箱不在此处修改，需走单独验证流程） */
export interface ProfileUpdateForm {
  realName: string
  phone: string
  gender: string
}

/** 获取当前用户资料 */
export function getProfile() {
  return get<UserProfile>('/user/profile/get')
}

/** 更新当前用户资料 */
export function updateProfile(form: ProfileUpdateForm) {
  return post('/user/profile/update', form)
}

/** 修改密码表单 */
export interface PasswordUpdateForm {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

/** 当前用户修改密码 */
export function changePassword(form: PasswordUpdateForm) {
  return post('/user/password/update', form)
}

/** 发送邮箱修改验证码 */
export interface EmailChangeCodeParams {
  newEmail: string
}

/** 确认修改邮箱 */
export interface EmailChangeParams {
  newEmail: string
  code: string
}

/** 发送邮箱修改验证码到新邮箱 */
export function sendEmailChangeCode(params: EmailChangeCodeParams) {
  return post('/user/email/change/code', params)
}

/** 校验验证码并修改邮箱 */
export function changeEmail(params: EmailChangeParams) {
  return post('/user/email/change', params)
}
