import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginService, logout as logoutService } from '@/services/auth'
import type { LoginResponse, User } from '@/types'
import { useStorage } from '@vueuse/core'

export const useAuthStore = defineStore('auth', () => {
  const user = useStorage('user', null as User | null)
  const token = useStorage('token', null as string | null)
  const isAuthenticated = ref(false)

  const login = async (email: string, password: string) => {
    try {
      const response: LoginResponse = await loginService(email, password)
      user.value = response.user
      token.value = response.token
      isAuthenticated.value = true
      return response
    } catch (error) {
      console.error('Login failed:', error)
      throw error
    }
  }

  const logout = async () => {
    try {
      await logoutService()
    } catch (error) {
      console.error('Logout failed:', error)
    } finally {
      user.value = null
      token.value = null
      isAuthenticated.value = false
      window.location.href = '/login'
    }
  }

  const checkAuth = () => {
    if (token.value) {
      isAuthenticated.value = true
    } else {
      isAuthenticated.value = false
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    logout,
    checkAuth,
  }
})
