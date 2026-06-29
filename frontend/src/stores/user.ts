// src/stores/user.ts
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    role: '',
    userName: '',
    email: '',
  }),
  actions: {
    setUser(token: string, role: string, userName: string, email: string) {
      this.token = token
      this.role = role
      this.userName = userName
      this.email = email
      localStorage.setItem('token', token)
      localStorage.setItem('userRole', role)
      localStorage.setItem('userName', userName)
      localStorage.setItem('userEmail', email)
    },
    logout() {
      this.token = ''
      this.role = ''
      this.userName = ''
      this.email = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userName')
      localStorage.removeItem('userEmail')
      localStorage.clear()
      sessionStorage.clear()
    },
    loadFromStorage() {
      this.token = localStorage.getItem('token') || ''
      this.role = localStorage.getItem('userRole') || ''
      this.userName = localStorage.getItem('userName') || ''
      this.email = localStorage.getItem('userEmail') || ''
    },
  },
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin',
    isCaregiver: (state) => state.role === 'caregiver',
  },
})
