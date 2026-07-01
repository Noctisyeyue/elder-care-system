import { defineStore } from 'pinia'

/**
 * 用户登录态存储。
 * 字段对齐后端 LoginResponseVO，刷新页面后从 sessionStorage 恢复。
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    /** 登录令牌。 */
    token: '',
    /** 用户 ID。 */
    userId: 0 as number,
    /** 真实姓名。 */
    realName: '',
    /** 邮箱。 */
    email: '',
    /** 角色 ID。 */
    roleId: 0 as number,
    /** 角色标识：super_admin / admin / caregiver。 */
    roleKey: '',
    /** 角色名（中文，仅展示用）。 */
    roleName: '',
    /** 账号状态：0 待审核，1 启用，2 禁用。 */
    status: 1 as number,
  }),
  actions: {
    /**
     * 保存登录态并同步到 sessionStorage。
     *
     * @param data 登录返回的用户信息
     * @returns 无返回值
     */
    setUser(data: {
      token: string
      userId: number
      realName: string
      email: string
      roleId: number
      roleKey: string
      roleName: string
      status: number
    }) {
      this.token = data.token
      this.userId = data.userId
      this.realName = data.realName
      this.email = data.email
      this.roleId = data.roleId
      this.roleKey = data.roleKey
      this.roleName = data.roleName
      this.status = data.status
      sessionStorage.setItem('token', data.token)
      sessionStorage.setItem('userId', String(data.userId))
      sessionStorage.setItem('realName', data.realName)
      sessionStorage.setItem('email', data.email)
      sessionStorage.setItem('roleId', String(data.roleId))
      sessionStorage.setItem('roleKey', data.roleKey)
      sessionStorage.setItem('roleName', data.roleName)
      sessionStorage.setItem('status', String(data.status))
    },
    /**
     * 清理登录态。
     *
     * @returns 无返回值
     */
    logout() {
      this.token = ''
      this.userId = 0
      this.realName = ''
      this.email = ''
      this.roleId = 0
      this.roleKey = ''
      this.roleName = ''
      this.status = 1
      ;[
        'token',
        'userId',
        'realName',
        'email',
        'roleId',
        'roleKey',
        'roleName',
        'status',
      ].forEach((k) => {
        sessionStorage.removeItem(k)
        localStorage.removeItem(k)
      })
      // 延迟导入避免循环依赖
      import('@/stores/menu').then(({ useMenuStore }) => useMenuStore().clearMenuList())
      import('@/stores/worktab').then(({ useWorktabStore }) => useWorktabStore().clearTabs())
    },
    /**
     * 从会话存储中恢复登录态。
     *
     * @returns 无返回值
     */
    loadFromStorage() {
      this.token = sessionStorage.getItem('token') || ''
      this.userId = Number(sessionStorage.getItem('userId')) || 0
      this.realName = sessionStorage.getItem('realName') || ''
      this.email = sessionStorage.getItem('email') || ''
      this.roleId = Number(sessionStorage.getItem('roleId')) || 0
      this.roleKey = sessionStorage.getItem('roleKey') || ''
      this.roleName = sessionStorage.getItem('roleName') || ''
      this.status = Number(sessionStorage.getItem('status')) || 1
    },
  },
  getters: {
    /** 是否已登录。 */
    isLoggedIn: (state) => !!state.token,
    /** 是否为超级管理员。 */
    isSuperAdmin: (state) => state.roleKey === 'super_admin',
    /** 是否为管理员（含超级管理员，二者都有业务管理权限）。 */
    isAdmin: (state) => state.roleKey === 'admin' || state.roleKey === 'super_admin',
    /** 是否为护工。 */
    isCaregiver: (state) => state.roleKey === 'caregiver',
    /** 是否待审核。 */
    isPending: (state) => state.status === 0,
  },
})
