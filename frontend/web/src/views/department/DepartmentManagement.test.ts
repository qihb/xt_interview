import { beforeEach, describe, expect, it, vi } from 'vitest'
import { config, flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import DepartmentManagement from './DepartmentManagement.vue'
import { useUserStore } from '@/features/auth'
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
  'el-table': {
    props: ['data'],
    template: '<div class="table-stub"><slot v-for="row in data" :row="row" /></div>'
  },
  'el-table-column': {
    template: '<div><slot :row="{}" /></div>'
  },
  'el-dialog': true,
  'el-form': true,
  'el-form-item': true,
  'el-input': true,
  'el-select': true,
  'el-option': true,
  'el-button': {
    template: '<button><slot /></button>'
  },
  'el-tag': true,
  'el-pagination': true,
  DepartmentSelect: true,
  DepartmentDialog: true
}

describe('DepartmentManagement', () => {
  beforeEach(() => {
    const pinia = createPinia()
    setActivePinia(pinia)
    config.global.plugins = [pinia]
    useUserStore().setUserInfo({ id: 1, username: 'viewer', roleLevel: 1 })
    vi.mocked(departmentApi.getAll).mockResolvedValue([])
  })

  it('loads departments on mount', () => {
    vi.mocked(departmentApi.getAll).mockResolvedValueOnce([
      { id: 1, name: '研发部', code: 'RD', status: 1 }
    ])

    mount(DepartmentManagement, { global: { stubs, directives: { loading: () => undefined } } })

    expect(departmentApi.getAll).toHaveBeenCalledOnce()
  })

  it('hides mutation actions for a viewer', () => {
    const wrapper = mount(DepartmentManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })

    expect(wrapper.text()).not.toContain('新增部门')
    expect(wrapper.text()).not.toContain('编辑')
    expect(wrapper.text()).not.toContain('删除')
    expect(wrapper.text()).toContain('刷新')
  })

  it('shows all mutation actions for a super administrator', async () => {
    useUserStore().setUserInfo({ id: 3, username: 'root', roleLevel: 3 })
    expect(useUserStore().can('create')).toBe(true)
    vi.mocked(departmentApi.getAll).mockResolvedValue([
      { id: 1, name: '研发部', code: 'RD', status: 1 }
    ])
    const wrapper = mount(DepartmentManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })
    await flushPromises()
    expect((wrapper.vm as any).userStore.can('create')).toBe(true)

    expect(wrapper.text()).toContain('新增部门')
    expect(wrapper.text()).toContain('编辑')
    expect(wrapper.text()).toContain('删除')
  })
})
