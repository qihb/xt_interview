import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserManagement from './UserManagement.vue'
import { accountApi } from '@/features/account/api'

vi.mock('@/features/account/api', () => ({
  accountApi: {
    page: vi.fn(),
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
  DepartmentSelect: true,
  AccountRoleSelect: true,
  AccountStatusSelect: true,
  UserDialog: true
}

describe('UserManagement', () => {
  it('loads users on mount', () => {
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
})
