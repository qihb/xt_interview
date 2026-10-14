import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { departmentApi, type Department } from '../api'

export function useDepartmentList() {
  const loading = ref(false)
  const departments = ref<Department[]>([])

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

  return {
    loading,
    departments,
    loadDepartments
  }
}
