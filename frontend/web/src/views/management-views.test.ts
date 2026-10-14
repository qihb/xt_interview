import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserManagement from '@/views/UserManagement.vue'
import DepartmentManagement from '@/views/DepartmentManagement.vue'
import { accountApi } from '@/features/account/api'
import { departmentApi } from '@/features/department/api'

vi.mock('@/features/account/api', () => ({
  accountApi: {
    page: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn()
  }
}))

vi.mock('@/features/department/api', () => ({
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
  'el-pagination': true,
  DepartmentSelect: true
}

describe('management views', () => {
  it('loads users on mount', async () => {
    vi.mocked(accountApi.page).mockResolvedValueOnce({
      list: [{ id: 1, username: 'alice' }],
      totalElements: 1,
      totalPages: 1,
      currentPage: 0,
      pageSize: 10
    } as any)

    mount(UserManagement, { global: { stubs, directives: { loading: () => undefined } } })

    expect(accountApi.page).toHaveBeenCalledWith({
      username: '',
      status: undefined,
      roleLevel: undefined,
      page: 0,
      size: 10
    })
  })

  it('loads departments on mount', async () => {
    vi.mocked(departmentApi.getAll).mockResolvedValueOnce([
      { id: 1, name: '研发部', code: 'RD', status: 1 }
    ])

    mount(DepartmentManagement, { global: { stubs, directives: { loading: () => undefined } } })

    expect(departmentApi.getAll).toHaveBeenCalledOnce()
  })
})
