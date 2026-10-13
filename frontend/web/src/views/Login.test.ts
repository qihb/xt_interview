import { describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Login from '@/views/Login.vue'

vi.mock('vue-router', async (importOriginal) => {
  const actual = await importOriginal<typeof import('vue-router')>()
  return {
    ...actual,
    useRouter: () => ({ push: vi.fn() }),
    useRoute: () => ({ query: {} })
  }
})

describe('Login view', () => {
  it('renders required login fields and the default account hint', () => {
    const wrapper = mount(Login, {
      global: {
        stubs: {
          'el-form': { template: '<form><slot /></form>' },
          'el-form-item': { template: '<div><slot /></div>' },
          'el-input': { inheritAttrs: false, template: '<input />' },
          'el-button': { template: '<button><slot /></button>' }
        }
      }
    })

    expect(wrapper.text()).toContain('用户登录')
    expect(wrapper.text()).toContain('默认账号：admin / 123456')
    expect(wrapper.findAll('input')).toHaveLength(2)
  })
})
