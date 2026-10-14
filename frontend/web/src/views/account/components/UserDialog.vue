<template>
  <el-dialog
    v-model="visible"
    :title="mode === 'edit' ? '编辑用户' : '添加用户'"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="90px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="formData.username"
          maxlength="20"
          show-word-limit
          placeholder="请输入 3-20 位字母、数字或下划线"
        />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="formData.name" maxlength="20" show-word-limit placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
      </el-form-item>
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="formData.phone" maxlength="20" show-word-limit placeholder="请输入手机号或座机号" />
      </el-form-item>
      <el-form-item label="所属部门" prop="departmentId">
        <DepartmentSelect v-model="formData.departmentId" scope="active" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          show-password
          maxlength="20"
          show-word-limit
          :placeholder="mode === 'edit' ? '留空则不修改密码' : '留空则默认123456'"
        />
      </el-form-item>
      <el-form-item label="权限级别" prop="roleLevel">
        <AccountRoleSelect
          v-model="formData.roleLevel"
          :clearable="false"
          placeholder="请选择权限级别"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <AccountStatusSelect
          v-model="formData.status"
          :clearable="false"
          placeholder="请选择状态"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { DepartmentSelect } from '@/features/department'
import { AccountRoleSelect, AccountStatusSelect, type AccountPayload } from '@/features/account'

type UserDialogMode = 'create' | 'edit'

const defaultFormData: AccountPayload = {
  username: '',
  name: '',
  gender: '男',
  phone: '',
  password: '',
  roleLevel: 1,
  status: 1,
  isSuper: 0,
  departmentId: null
}

const props = withDefaults(defineProps<{
  modelValue: boolean
  mode?: UserDialogMode
  initialData?: Partial<AccountPayload>
  submitting?: boolean
}>(), {
  mode: 'create',
  initialData: undefined,
  submitting: false
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [data: AccountPayload]
}>()

const formRef = ref<FormInstance>()
const formData = reactive<AccountPayload>({ ...defaultFormData })

const visible = ref(props.modelValue)
watch(() => props.modelValue, (value) => {
  visible.value = value
  if (value) syncForm()
})
watch(visible, (value) => emit('update:modelValue', value))

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为 3-20 个字符', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应为 2-20 个字', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5A-Za-z·\s]+$/, message: '姓名只能包含中文、字母或间隔点', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^(1[3-9]\d{9}|0\d{2,3}-?\d{7,8})$/, message: '请输入正确的手机号或座机号', trigger: 'blur' }
  ],
  password: [
    {
      validator: (_rule, value, callback) => {
        if (!value) return callback()
        if (value.length < 6 || value.length > 20) return callback(new Error('密码长度应为 6-20 个字符'))
        callback()
      },
      trigger: 'blur'
    }
  ],
  roleLevel: [{ required: true, message: '请选择权限级别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function syncForm() {
  Object.assign(formData, defaultFormData, props.initialData || {})
  formRef.value?.clearValidate()
}

function handleClose() {
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  emit('submit', { ...formData })
}
</script>
