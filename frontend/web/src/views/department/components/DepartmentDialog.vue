<template>
  <el-dialog
    v-model="visible"
    :title="mode === 'edit' ? '编辑部门' : '新增部门'"
    width="500px"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="formData.name" maxlength="20" show-word-limit placeholder="请输入部门名称" />
      </el-form-item>
      <el-form-item label="部门编码" prop="code">
        <el-input v-model="formData.code" maxlength="20" show-word-limit placeholder="请输入部门编码" />
      </el-form-item>
      <el-form-item label="部门描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          maxlength="200"
          show-word-limit
          placeholder="请输入部门描述"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" style="width: 100%">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
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
import type { DepartmentPayload } from '@/features/department'

type DepartmentDialogMode = 'create' | 'edit'

const defaultFormData: DepartmentPayload = {
  name: '',
  code: '',
  description: '',
  status: 1
}

const props = withDefaults(defineProps<{
  modelValue: boolean
  mode?: DepartmentDialogMode
  initialData?: Partial<DepartmentPayload>
  submitting?: boolean
}>(), {
  mode: 'create',
  initialData: undefined,
  submitting: false
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  submit: [data: DepartmentPayload]
}>()

const formRef = ref<FormInstance>()
const formData = reactive<DepartmentPayload>({ ...defaultFormData })
const visible = ref(props.modelValue)

watch(() => props.modelValue, (value) => {
  visible.value = value
  if (value) syncForm()
})
watch(visible, (value) => emit('update:modelValue', value))

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 20, message: '部门名称长度应为 2-20 个字', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value && value.trim() !== value) return callback(new Error('部门名称首尾不能有空格'))
        callback()
      },
      trigger: 'blur'
    }
  ],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' },
    { min: 2, max: 20, message: '部门编码长度应为 2-20 个字符', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_-]+$/, message: '部门编码只能包含字母、数字、下划线或短横线', trigger: 'blur' }
  ],
  description: [{ max: 200, message: '部门描述不能超过 200 个字', trigger: 'blur' }],
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
