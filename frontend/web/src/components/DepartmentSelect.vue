<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { departmentApi, type Department } from '@/api'

defineOptions({ name: 'DepartmentSelect' })

const props = withDefaults(defineProps<{
  scope?: 'active' | 'all'
  clearable?: boolean
  disabled?: boolean
  placeholder?: string
}>(), {
  scope: 'active',
  clearable: true,
  disabled: false,
  placeholder: '请选择部门'
})

const model = defineModel<number | null>({ default: null })
const departments = ref<Department[]>([])
const loading = ref(false)

const options = computed(() => {
  if (props.scope === 'all') {
    return departments.value
  }
  return departments.value.filter((department) => department.status === 1)
})

async function loadDepartments() {
  loading.value = true
  try {
    departments.value = await departmentApi.getAll()
  } catch (error: any) {
    ElMessage.error(error.message || '加载部门列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadDepartments)
</script>

<template>
  <el-select
    v-model="model"
    :clearable="clearable"
    :disabled="disabled"
    :loading="loading"
    :placeholder="placeholder"
    style="width: 100%"
  >
    <el-option
      v-for="department in options"
      :key="department.id"
      :label="department.name"
      :value="department.id"
    />
  </el-select>
</template>
