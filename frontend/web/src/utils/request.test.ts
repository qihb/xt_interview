import { afterEach, describe, expect, it, vi } from 'vitest'

const push = vi.fn()
const messageError = vi.fn()

vi.mock('@/router', () => ({ default: { push } }))
vi.mock('element-plus', () => ({ ElMessage: { error: messageError } }))

describe('request client', () => {
  afterEach(() => {
    vi.resetModules()
    push.mockReset()
    messageError.mockReset()
    localStorage.clear()
  })

  it('adds the bearer token and returns response data', async () => {
    localStorage.setItem('token', 'abc')
    const request = (await import('@/utils/request')).default
    const config = await request.interceptors.request.handlers[0].fulfilled({
      headers: {}
    })
    const result = request.interceptors.response.handlers[0].fulfilled({ data: { ok: true } })

    expect(config.headers.Authorization).toBe('Bearer abc')
    expect(result).toEqual({ ok: true })
  })

  it('clears session and redirects on unauthorized responses', async () => {
    localStorage.setItem('token', 'abc')
    localStorage.setItem('currentUser', '{}')
    const request = (await import('@/utils/request')).default

    await expect(request.interceptors.response.handlers[0].rejected({
      response: { status: 401 }
    })).rejects.toBeTruthy()

    expect(localStorage.getItem('token')).toBeNull()
    expect(push).toHaveBeenCalledWith('/login')
    expect(messageError).toHaveBeenCalledWith('登录已过期，请重新登录')
  })

  it('passes conflict business messages to callers', async () => {
    const request = (await import('@/utils/request')).default
    const responseData = { message: '部门已被用户关联，无法删除' }

    await expect(request.interceptors.response.handlers[0].rejected({
      response: { status: 409, data: responseData }
    })).rejects.toEqual(responseData)

    expect(messageError).not.toHaveBeenCalled()
  })

  it('shows the backend permission message for forbidden responses', async () => {
    const request = (await import('@/utils/request')).default
    const responseData = {
      status: 403,
      message: '没有执行该操作的权限',
      details: { permission: '需要超级管理员权限' }
    }

    await expect(request.interceptors.response.handlers[0].rejected({
      response: { status: 403, data: responseData }
    })).rejects.toBeTruthy()

    expect(messageError).toHaveBeenCalledWith('没有执行该操作的权限')
  })
})
