import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import DepartmentTree from './DepartmentTree.vue'
import { departmentApi } from '@/features/department/api'

vi.mock('@/features/department/api', () => ({
  departmentApi: {
    getAll: vi.fn()
  }
}))

describe('DepartmentTree', () => {
  beforeEach(() => {
    vi.mocked(departmentApi.getAll).mockResolvedValue([
      { id: 2, name: '研发部', code: 'RD', status: 1 },
      { id: 3, name: '停用部', code: 'OLD', status: 0 }
    ])
  })

  it('loads departments and renders the all root node', async () => {
    const wrapper = mount(DepartmentTree, {
      global: {
        stubs: {
          'el-tree': {
            props: ['data'],
            template: '<div class="tree-stub">{{ data[0]?.label }} {{ data[0]?.children?.map((item) => item.label).join(",") }}</div>'
          }
        },
        directives: { loading: () => undefined }
      }
    })

    await flushPromises()

    expect(departmentApi.getAll).toHaveBeenCalled()
    expect(wrapper.text()).toContain('部门')
    expect(wrapper.find('.tree-stub').text()).toContain('全部')
    expect(wrapper.find('.tree-stub').text()).toContain('研发部')
    expect(wrapper.find('.tree-stub').text()).not.toContain('停用部')
  })

  it('emits the selected department id', async () => {
    const wrapper = mount(DepartmentTree, {
      global: {
        stubs: {
          'el-tree': {
            emits: ['node-click'],
            template: '<button @click="$emit(\'node-click\', { id: 2, departmentId: 2 })">选择</button>'
          }
        },
        directives: { loading: () => undefined }
      }
    })

    await wrapper.find('button').trigger('click')

    expect(wrapper.emitted('select')).toEqual([[2]])
  })
})
