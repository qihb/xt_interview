import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import DepartmentManagement from './DepartmentManagement.vue'
import { departmentApi } from '@/features/department/api'

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
  DepartmentSelect: true,
  DepartmentDialog: true
}

describe('DepartmentManagement', () => {
  it('loads departments on mount', () => {
    vi.mocked(departmentApi.getAll).mockResolvedValueOnce([
      { id: 1, name: '研发部', code: 'RD', status: 1 }
    ])

    mount(DepartmentManagement, { global: { stubs, directives: { loading: () => undefined } } })

    expect(departmentApi.getAll).toHaveBeenCalledOnce()
  })
})
