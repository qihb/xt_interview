import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserManagement from '@/views/UserManagement.vue'
import DepartmentManagement from '@/views/DepartmentManagement.vue'
import { accountApi, departmentApi } from '@/api'

vi.mock('@/api', () => ({
  accountApi: {
    getAll: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn()
  },
  departmentApi: {
    getAll: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn()
  }
}))

const stubs = {
  'el-table': true,
  'el-table-column': true,
  'el-dialog': true,
  'el-form': true,
  'el-form-item': true,
  'el-input': true,
  'el-select': true,
  'el-option': true,
  'el-button': true,
  'el-tag': true,
  DepartmentSelect: true
}

describe('management views', () => {
  it('loads users on mount', async () => {
    vi.mocked(accountApi.getAll).mockResolvedValueOnce([{ id: 1, username: 'alice' }] as any)

    mount(UserManagement, { global: { stubs, directives: { loading: () => undefined } } })

    expect(accountApi.getAll).toHaveBeenCalledOnce()
  })

  it('loads departments on mount', async () => {
    vi.mocked(departmentApi.getAll).mockResolvedValueOnce([
      { id: 1, name: '研发部', code: 'RD', status: 1 }
    ])

    mount(DepartmentManagement, { global: { stubs, directives: { loading: () => undefined } } })

    expect(departmentApi.getAll).toHaveBeenCalledOnce()
  })
})
