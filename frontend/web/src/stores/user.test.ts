import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api'

vi.mock('@/api', () => ({
  authApi: { validate: vi.fn() }
}))

describe('user store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.spyOn(console, 'error').mockImplementation(() => undefined)
  })

  it('persists token and user information, then clears both on logout', () => {
    const store = useUserStore()
    const user = { id: 1, username: 'admin' }

    store.setToken('token-1')
    store.setUserInfo(user)
    localStorage.setItem('currentUser', JSON.stringify(user))
    store.logout()

    expect(store.isLoggedIn).toBe(false)
    expect(store.userInfo).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()
    expect(localStorage.getItem('currentUser')).toBeNull()
  })

  it('validates an existing token and handles validation failures', async () => {
    const store = useUserStore()
    store.setToken('token-1')
    vi.mocked(authApi.validate).mockResolvedValueOnce({ valid: true } as any)
    expect(await store.validateToken()).toEqual({ valid: true })

    vi.mocked(authApi.validate).mockRejectedValueOnce(new Error('expired'))
    expect(await store.validateToken()).toEqual({ valid: false })
  })

  it('loads the persisted current user', () => {
    const store = useUserStore()
    localStorage.setItem('currentUser', JSON.stringify({ id: 2 }))
    store.loadFromStorage()
    expect(store.userInfo).toEqual({ id: 2 })
  })
})
