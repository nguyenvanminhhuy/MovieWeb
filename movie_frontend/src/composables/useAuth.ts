import { computed, ref } from 'vue'
import type { UserResponse } from '../api/types'
import * as api from '../api/animeApi'

const token = ref<string | null>(localStorage.getItem('access_token'))
const user = ref<UserResponse | null>(null)
const loading = ref(false)

export function useAuth() {
  const isLoggedIn = computed(() => !!token.value)

  async function refreshProfile() {
    if (!token.value) {
      user.value = null
      return
    }
    loading.value = true
    try {
      user.value = await api.getMyInfo()
    } catch {
      user.value = null
      token.value = null
      localStorage.removeItem('access_token')
    } finally {
      loading.value = false
    }
  }

  function setToken(t: string) {
    token.value = t
    localStorage.setItem('access_token', t)
  }

  function clearSession() {
    token.value = null
    localStorage.removeItem('access_token')
    user.value = null
  }

  async function login(username: string, password: string) {
    const res = await api.login(username, password)
    if (res.token) setToken(res.token)
    await refreshProfile()
  }

  async function logout() {
    try {
      await api.logout()
    } catch {
      /* ignore */
    }
    clearSession()
  }

  const isAdmin = computed(() => {
    return user.value?.roles?.some((r) => r.name === 'ADMIN') ?? false
  })

  return {
    token,
    user,
    loading,
    isLoggedIn,
    isAdmin,
    refreshProfile,
    setToken,
    clearSession,
    login,
    logout,
  }
}
