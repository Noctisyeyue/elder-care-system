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
      sessionStorage.setItem('token', token)
      sessionStorage.setItem('userRole', role)
      sessionStorage.setItem('userName', userName)
      sessionStorage.setItem('userEmail', email)
    },
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
    loadFromStorage() {
      this.token = sessionStorage.getItem('token') || ''
      this.role = sessionStorage.getItem('userRole') || ''
      this.userName = sessionStorage.getItem('userName') || ''
      this.email = sessionStorage.getItem('userEmail') || ''
    },
  },
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin',
    isCaregiver: (state) => state.role === 'caregiver',
  },
})
