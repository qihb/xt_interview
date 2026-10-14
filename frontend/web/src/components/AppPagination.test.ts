import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import AppPagination from '@/components/AppPagination.vue'

describe('AppPagination', () => {
  it('converts Element Plus one-based pages to zero-based updates', async () => {
    const wrapper = mount(AppPagination, {
      props: { currentPage: 0, pageSize: 10, total: 25 },
      global: {
        stubs: {
          'el-pagination': {
            template: '<button @click="$emit(\'current-change\', 2)">page</button>'
          }
        }
      }
    })

    await wrapper.find('button').trigger('click')

    expect(wrapper.emitted('page-change')).toEqual([[1]])
  })
})
