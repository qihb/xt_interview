import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { accountApi, type Account } from '../api'

export function useAccountList() {
  const loading = ref(false)
  const users = ref<Account[]>([])
  const total = ref(0)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const filters = reactive<{
    username: string
    status?: number
    roleLevel?: number
  }>({
    username: '',
    status: undefined,
    roleLevel: undefined
  })

  async function loadUsers() {
    loading.value = true
    try {
      const result = await accountApi.page({
        username: filters.username,
        status: filters.status,
        roleLevel: filters.roleLevel,
        page: currentPage.value,
        size: pageSize.value
      })
      users.value = result.list
      total.value = result.totalElements
    } catch (error: any) {
      ElMessage.error(error.message || '加载用户列表失败')
    } finally {
      loading.value = false
    }
  }

  function handleSearch() {
    currentPage.value = 0
    return loadUsers()
  }

  function handlePageChange(page: number) {
    currentPage.value = page
    return loadUsers()
  }

  function handlePageSizeChange(size: number) {
    pageSize.value = size
    currentPage.value = 0
    return loadUsers()
  }

  return {
    loading,
    users,
    total,
    currentPage,
    pageSize,
    filters,
    loadUsers,
    handleSearch,
    handlePageChange,
    handlePageSizeChange
  }
}
