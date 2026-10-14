import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import DepartmentDialog from './DepartmentDialog.vue'

const DialogStub = {
  name: 'DialogStub',
  props: ['modelValue'],
  emits: ['close'],
  template: '<div><slot /></div>'
}

const FormStub: any = {
  template: '<form><slot /></form>',
  setup(_props: unknown, { expose }: { expose: (value: unknown) => void }) {
    expose({
      clearValidate: () => undefined,
      resetFields: () => undefined
    })
    return {}
  }
}

describe('DepartmentDialog', () => {
  it('keeps a disabled status when the dialog closes', async () => {
    const wrapper = mount(DepartmentDialog, {
      props: {
        modelValue: false,
        mode: 'edit',
        initialData: {
          name: '研发部',
          code: 'RD',
          status: 0
        }
      },
      global: {
        stubs: {
          'el-dialog': DialogStub,
          'el-form': FormStub,
          'el-form-item': { template: '<div><slot /></div>' },
          'el-input': true,
          'el-select': true,
          'el-option': true,
          'el-button': true
        }
      }
    })

    await wrapper.setProps({ modelValue: true })
    await wrapper.findComponent(DialogStub).vm.$emit('close')

    expect((wrapper.vm as any).formData.status).toBe(0)
  })
})
