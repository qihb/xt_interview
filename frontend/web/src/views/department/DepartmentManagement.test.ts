import { beforeEach, describe, expect, it, vi } from 'vitest'
import { config, flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import DepartmentManagement from './DepartmentManagement.vue'
import { useUserStore } from '@/features/auth'
import { departmentApi } from '@/features/department/api'
import { ElMessageBox } from 'element-plus'

vi.mock('element-plus', async () => {
  const actual = await vi.importActual<typeof import('element-plus')>('element-plus')
  return {
    ...actual,
    ElMessage: { success: vi.fn(), error: vi.fn() },
    ElMessageBox: { confirm: vi.fn() }
  }
})

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

  it('confirms user unassignment and retries disabling the department', async () => {
    useUserStore().setUserInfo({ id: 3, username: 'root', roleLevel: 3 })
    vi.mocked(departmentApi.getAll).mockResolvedValue([])
    vi.mocked(departmentApi.update)
      .mockRejectedValueOnce({
        message: '禁用部门将把关联用户改为未分配，请确认',
        details: { requiresConfirmation: 'true', accountCount: '3' }
      })
      .mockResolvedValueOnce({ id: 1, name: '研发部', code: 'RD', status: 0 })
    vi.mocked(ElMessageBox.confirm).mockResolvedValueOnce(true as any)

    const wrapper = mount(DepartmentManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })
    await flushPromises()
    ;(wrapper.vm as any).openEditDialog({ id: 1, name: '研发部', code: 'RD', status: 1 })
    await (wrapper.vm as any).handleSubmit({
      name: '研发部',
      code: 'RD',
      description: '',
      status: 0
    })

    expect(ElMessageBox.confirm).toHaveBeenCalled()
    expect(departmentApi.update).toHaveBeenNthCalledWith(
      2,
      1,
      expect.objectContaining({ status: 0, unassignUsers: true })
    )
  })

  it('does not retry disabling when the user cancels confirmation', async () => {
    useUserStore().setUserInfo({ id: 3, username: 'root', roleLevel: 3 })
    vi.mocked(departmentApi.getAll).mockResolvedValue([])
    vi.mocked(departmentApi.update).mockRejectedValueOnce({
      message: '禁用部门将把关联用户改为未分配，请确认',
      details: { requiresConfirmation: 'true', accountCount: '2' }
    })
    vi.mocked(ElMessageBox.confirm).mockRejectedValueOnce('cancel')

    const wrapper = mount(DepartmentManagement, {
      global: { stubs, directives: { loading: () => undefined } }
    })
    await flushPromises()
    ;(wrapper.vm as any).openEditDialog({ id: 1, name: '研发部', code: 'RD', status: 1 })
    await (wrapper.vm as any).handleSubmit({
      name: '研发部',
      code: 'RD',
      description: '',
      status: 0
    })

    expect(departmentApi.update).toHaveBeenCalledOnce()
  })
})
