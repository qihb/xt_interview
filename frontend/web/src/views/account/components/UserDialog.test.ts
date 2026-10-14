import { describe, expect, it } from 'vitest'
import { mount } from '@vue/test-utils'
import UserDialog from './UserDialog.vue'

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

describe('UserDialog', () => {
  it('keeps a disabled status when the dialog closes', async () => {
    const wrapper = mount(UserDialog, {
      props: {
        modelValue: false,
        mode: 'edit',
        initialData: {
          username: 'alice',
          name: 'Alice',
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
          DepartmentSelect: true,
          AccountRoleSelect: true,
          AccountStatusSelect: true,
          'el-button': true
        }
      }
    })

    await wrapper.setProps({ modelValue: true })
    await wrapper.findComponent(DialogStub).vm.$emit('close')

    expect((wrapper.vm as any).formData.status).toBe(0)
  })
})
