import { describe, expect, it, vi } from 'vitest'
import { nextTick } from 'vue'
import { mount } from '@vue/test-utils'
import DepartmentSelect from './DepartmentSelect.vue'
import { departmentApi } from '@/features/department'

vi.mock('@/features/department', () => ({
  departmentApi: {
    getAll: vi.fn()
  }
}))

describe('DepartmentSelect', () => {
  it('filters disabled departments in active scope', async () => {
    vi.mocked(departmentApi.getAll).mockResolvedValueOnce([
      { id: 1, name: '研发部', code: 'RD', status: 1 },
      { id: 2, name: '停用部', code: 'OLD', status: 0 }
    ])

    const wrapper = mount(DepartmentSelect, {
      props: { modelValue: null, scope: 'active' },
      global: {
        stubs: {
          'el-select': { template: '<div><slot /></div>' },
          'el-option': {
            props: ['label', 'value'],
            template: '<div class="department-option">{{ label }}</div>'
          }
        }
      }
    })

    await vi.waitFor(() => expect(departmentApi.getAll).toHaveBeenCalledOnce())
    await nextTick()
    expect(wrapper.findAll('.department-option')).toHaveLength(1)
  })

  it('includes disabled departments in all scope for historical values', async () => {
    vi.mocked(departmentApi.getAll).mockResolvedValueOnce([
      { id: 1, name: '研发部', code: 'RD', status: 1 },
      { id: 2, name: '停用部', code: 'OLD', status: 0 }
    ])

    const wrapper = mount(DepartmentSelect, {
      props: { modelValue: 2, scope: 'all' },
      global: {
        stubs: {
          'el-select': { template: '<div><slot /></div>' },
          'el-option': {
            props: ['label', 'value'],
            template: '<div class="department-option">{{ label }}</div>'
          }
        }
      }
    })

    await vi.waitFor(() => expect(departmentApi.getAll).toHaveBeenCalledOnce())
    await nextTick()
    expect(wrapper.findAll('.department-option')).toHaveLength(2)
  })
})
