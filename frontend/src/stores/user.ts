import { defineStore } from 'pinia'

/**
 * 用户登录态存储。
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    /** 登录令牌。 */
    token: '',
    /** 当前角色编码。 */
    role: '',
    /** 当前用户名。 */
    userName: '',
    /** 当前用户邮箱。 */
    email: '',
  }),
  actions: {
    /**
     * 保存登录态。
     *
     * @param token 登录令牌
     * @param role 角色编码
     * @param userName 用户名
     * @param email 邮箱
     * @returns 无返回值
     */
    setUser(token: string, role: string, userName: string, email: string) {
      this.token = token
      this.role = role
      this.userName = userName
      this.email = email
      sessionStorage.setItem('token', token)
      sessionStorage.setItem('userRole', role)
      sessionStorage.setItem('userName', userName)
      sessionStorage.setItem('userEmail', email)
    },
    /**
     * 清理登录态。
     *
     * @returns 无返回值
     */
    logout() {
      this.token = ''
      this.role = ''
      this.userName = ''
      this.email = ''
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('userRole')
      sessionStorage.removeItem('userName')
      sessionStorage.removeItem('userEmail')
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userName')
      localStorage.removeItem('userEmail')
    },
    /**
     * 从会话存储中恢复登录态。
     *
     * @returns 无返回值
     */
    loadFromStorage() {
      this.token = sessionStorage.getItem('token') || ''
      this.role = sessionStorage.getItem('userRole') || ''
      this.userName = sessionStorage.getItem('userName') || ''
      this.email = sessionStorage.getItem('userEmail') || ''
    },
  },
  getters: {
    /** 是否已登录。 */
    isLoggedIn: (state) => !!state.token,
    /** 是否为管理员。 */
    isAdmin: (state) => state.role === 'admin',
    /** 是否为健康管家。 */
    isCaregiver: (state) => state.role === 'caregiver',
  },
})
