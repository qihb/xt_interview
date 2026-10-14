import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserManagement from './UserManagement.vue'
import { accountApi } from '@/features/account/api'
import AccountRoleSelect from '@/features/account/components/AccountRoleSelect.vue'
import AccountStatusSelect from '@/features/account/components/AccountStatusSelect.vue'
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
    getAll: vi.fn()
  }
}))

const stubs = {
  'el-table': true,
  'el-table-column': true,
  'el-dialog': true,
  'el-form': {
    template: '<form><slot /></form>'
  },
  'el-form-item': {
    template: '<div><slot /></div>'
  },
  'el-input': {
    template: '<input v-bind="$attrs" />'
  },
  'el-select': true,
  'el-option': true,
  'el-button': {
    template: '<button><slot /></button>'
  },
  'el-tag': true,
  'el-pagination': true,
  'el-tree': {
    emits: ['node-click'],
    template: '<button class="department-tree-stub" @click="$emit(\'node-click\', { id: 2, departmentId: 2 })">部门树</button>'
  },
  DepartmentSelect: true,
  UserDialog: true
}

describe('UserManagement', () => {
  beforeEach(() => {
    vi.mocked(departmentApi.getAll).mockResolvedValue([])
  })

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
      departmentId: undefined,
      page: 0,
      size: 10
    })
  })

  it('renders status and role filters', () => {
    const wrapper = mount(UserManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })

    expect(wrapper.findComponent(AccountStatusSelect).exists()).toBe(true)
    expect(wrapper.findComponent(AccountRoleSelect).exists()).toBe(true)
  })

  it('renders a reset button beside search', () => {
    const wrapper = mount(UserManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })

    expect(wrapper.text()).toContain('重置')
  })

  it('uses an Element Plus form for user filters', () => {
    const wrapper = mount(UserManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })

    expect(wrapper.find('form.user-filters').exists()).toBe(true)
    expect(wrapper.findAll('form.user-filters > div')).toHaveLength(4)
  })

  it('searches when pressing enter in the username input', async () => {
    vi.mocked(accountApi.page).mockResolvedValue({
      list: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      pageSize: 10
    } as any)
    const wrapper = mount(UserManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })

    await wrapper.find('input').trigger('keyup.enter')

    expect(accountApi.page).toHaveBeenCalledTimes(2)
  })

  it('loads departments and filters users after selecting a department', async () => {
    vi.mocked(departmentApi.getAll).mockResolvedValue([
      { id: 2, name: '研发部', code: 'RD', status: 1 }
    ])
    vi.mocked(accountApi.page).mockResolvedValue({
      list: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      pageSize: 10
    } as any)

    const wrapper = mount(UserManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })

    await wrapper.find('.department-tree-stub').trigger('click')

    expect(departmentApi.getAll).toHaveBeenCalled()
    expect(accountApi.page).toHaveBeenLastCalledWith({
      username: '',
      status: undefined,
      roleLevel: undefined,
      departmentId: 2,
      page: 0,
      size: 10
    })
  })
})
