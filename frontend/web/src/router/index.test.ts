import { beforeEach, describe, expect, it } from 'vitest'
import router from '@/router'

describe('router guard', () => {
  beforeEach(async () => {
    localStorage.clear()
    await router.replace('/login')
  })

  it('redirects unauthenticated users to login with the original path', async () => {
    await router.push('/users')
    expect(router.currentRoute.value.name).toBe('Login')
    expect(router.currentRoute.value.query.redirect).toBe('/users')
  })

  it('allows authenticated users into protected pages and sets the title', async () => {
    localStorage.setItem('token', 'token-1')
    await router.push('/departments')
    expect(router.currentRoute.value.name).toBe('DepartmentManagement')
    expect(document.title).toBe('部门管理 - 欣铁 面试项目')
  })
})
