import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '../api'
import type { AuthUser } from '../api'
import { can as canPermission, type PermissionAction } from '../permissions'

function readStoredUser(): AuthUser | null {
  const stored = localStorage.getItem('currentUser')
  if (!stored) return null
  try {
    return JSON.parse(stored) as AuthUser
  } catch {
    return null
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const userInfo = ref<AuthUser | null>(readStoredUser())

  const isLoggedIn = computed(() => Boolean(token.value))

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info: AuthUser) {
    userInfo.value = info
    localStorage.setItem('currentUser', JSON.stringify(info))
  }

  function logout() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('currentUser')
  }

  async function validateToken() {
    if (!token.value) return { valid: false }
    try {
      return await authApi.validate()
    } catch (error) {
      console.error('Token验证失败', error)
      return { valid: false }
    }
  }

  function loadFromStorage() {
    userInfo.value = readStoredUser()
  }

  function can(action: PermissionAction) {
    return canPermission(userInfo.value?.roleLevel, action)
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    logout,
    validateToken,
    loadFromStorage,
    can
  }
})
