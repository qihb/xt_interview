import { describe, expect, it, vi } from 'vitest'
import request from '@/utils/request'
import { accountApi, authApi, departmentApi } from '@/api'

vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

describe('api clients', () => {
  it('maps authentication requests to the expected endpoints', () => {
    authApi.login({ username: 'admin', password: '123456' })
    authApi.logout()
    authApi.validate()

    expect(request.post).toHaveBeenNthCalledWith(1, '/auth/login', {
      username: 'admin',
      password: '123456'
    })
    expect(request.post).toHaveBeenNthCalledWith(2, '/auth/logout')
    expect(request.get).toHaveBeenCalledWith('/auth/validate')
  })

  it('maps account and department CRUD requests', () => {
    accountApi.getAll()
    accountApi.getById(1)
    accountApi.create({
      username: 'u',
      name: 'User',
      gender: '男',
      roleLevel: 1,
      status: 1,
      isSuper: 0,
      departmentId: null
    })
    accountApi.update(1, {
      username: 'u',
      name: 'N',
      gender: '男',
      roleLevel: 1,
      status: 1,
      isSuper: 0,
      departmentId: 2
    })
    accountApi.delete(1)
    departmentApi.getAll()
    departmentApi.getById(2)
    departmentApi.create({ name: '研发', code: 'RD', status: 1 })
    departmentApi.update(2, { name: '研发', code: 'RD', status: 0 })
    departmentApi.delete(2)

    expect(request.get).toHaveBeenCalledWith('/accounts')
    expect(request.get).toHaveBeenCalledWith('/accounts/1')
    expect(request.post).toHaveBeenCalledWith('/accounts', {
      username: 'u',
      name: 'User',
      gender: '男',
      roleLevel: 1,
      status: 1,
      isSuper: 0,
      departmentId: null
    })
    expect(request.put).toHaveBeenCalledWith('/accounts/1', {
      username: 'u',
      name: 'N',
      gender: '男',
      roleLevel: 1,
      status: 1,
      isSuper: 0,
      departmentId: 2
    })
    expect(request.delete).toHaveBeenCalledWith('/accounts/1')
    expect(request.get).toHaveBeenCalledWith('/departments')
    expect(request.get).toHaveBeenCalledWith('/departments/2')
    expect(request.post).toHaveBeenCalledWith('/departments', { name: '研发', code: 'RD', status: 1 })
    expect(request.put).toHaveBeenCalledWith('/departments/2', { name: '研发', code: 'RD', status: 0 })
    expect(request.delete).toHaveBeenCalledWith('/departments/2')
  })

  it('maps account page queries to the zero-based pagination endpoint', () => {
    accountApi.page({ username: 'ali', status: 1, roleLevel: 2, page: 0, size: 10 })

    expect(request.get).toHaveBeenCalledWith('/accounts/page', {
      params: { username: 'ali', status: 1, roleLevel: 2, page: 0, size: 10 }
    })
  })
})
