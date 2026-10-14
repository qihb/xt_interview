<template>
  <div class="page-container">
    <div class="user-layout">
      <DepartmentTree @select="handleDepartmentSelect" />

      <section class="user-section">
      <div class="user-header">
        <div class="user-header-title">
          <span>用户列表</span>
          <span class="user-count">共 {{ total }} 个用户</span>
        </div>
        <div class="user-header-actions">
          <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">
            新增用户
          </el-button>
        </div>
      </div>

      <el-form
        :model="filters"
        inline
        class="user-filters"
        @submit.prevent="handleSearch"
      >
        <el-form-item label="用户名">
          <el-input
            v-model="filters.username"
            clearable
            placeholder="请输入用户名"
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <AccountStatusSelect v-model="filters.status" />
        </el-form-item>
        <el-form-item label="权限级别">
          <AccountRoleSelect v-model="filters.roleLevel" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="users"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="phone" label="联系方式" width="140" />
        <el-table-column label="所属部门" width="140">
          <template #default="{ row }">
            {{ row.departmentName || '未分配' }}
          </template>
        </el-table-column>
        <el-table-column label="权限级别" width="120">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.roleLevel)">
              {{ roleText(row.roleLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <AppPagination
        class="user-pagination"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @page-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
      </section>
    </div>

    <UserDialog
      v-model="dialogVisible"
      :mode="isEdit ? 'edit' : 'create'"
      :initial-data="formData"
      :submitting="submitting"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  accountApi,
  AccountRoleSelect,
  AccountStatusSelect,
  type Account,
  type AccountPayload,
  useAccountList
} from '@/features/account'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import AppPagination from '@/components/AppPagination.vue'
import UserDialog from './components/UserDialog.vue'
import DepartmentTree from './components/DepartmentTree.vue'

const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number>(0)

const {
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
} = useAccountList()

const formData = reactive<AccountPayload>({
  username: '',
  name: '',
  gender: '男',
  phone: '',
  password: '',
  roleLevel: 1,
  status: 1,
  isSuper: 0,
  departmentId: null
})

function roleText(level: number): string {
  const map: Record<number, string> = { 1: '用户权限', 2: '管理员权限', 3: '超级管理员' }
  return map[level] || '未知'
}

function roleTagType(level: number): 'info' | 'warning' | 'danger' {
  const map: Record<number, 'info' | 'warning' | 'danger'> = {
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return map[level] || 'info'
}

function resetSearch() {
  filters.username = ''
  filters.status = undefined
  filters.roleLevel = undefined
  handleSearch()
}

function handleDepartmentSelect(departmentId?: number) {
  filters.departmentId = departmentId
  handleSearch()
}

function resetForm() {
  formData.username = ''
  formData.name = ''
  formData.gender = '男'
  formData.phone = ''
  formData.password = ''
  formData.roleLevel = 1
  formData.status = 1
  formData.isSuper = 0
  formData.departmentId = null
  isEdit.value = false
  editId.value = 0
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: Account) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  formData.username = row.username
  formData.name = row.name
  formData.gender = row.gender || '男'
  formData.phone = row.phone || ''
  formData.roleLevel = row.roleLevel
  formData.status = row.status
  formData.isSuper = row.isSuper
  formData.departmentId = row.departmentId ?? null
  dialogVisible.value = true
}

async function handleSubmit(data: AccountPayload) {
  submitting.value = true
  try {
    if (data.password === '') delete data.password
    if (isEdit.value) {
      await accountApi.update(editId.value, data)
      ElMessage.success('用户更新成功')
    } else {
      await accountApi.create(data)
      ElMessage.success('用户创建成功')
    }
    dialogVisible.value = false
    await loadUsers()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: Account) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await accountApi.delete(row.id)
    ElMessage.success('用户删除成功')
    await loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style lang="less" scoped>
@color-primary: #1764e8;
@text-primary: rgba(0, 0, 0, 0.85);
@text-secondary: rgba(0, 0, 0, 0.65);
@page-bg: #f5f7fa;

.page-container {
  min-height: 100%;
  background: @page-bg;
}

.user-layout {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.user-section {
  min-width: 0;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.user-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.user-header-title {
  display: flex;
  align-items: baseline;
  gap: 12px;
  color: @text-primary;
  font-size: 18px;
  font-weight: 600;
}

.user-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;

  :deep(.el-form-item) {
    margin-right: 0;
    margin-bottom: 0;
  }
}

.user-pagination {
  justify-content: flex-end;
  margin-top: 20px;
}

.user-count {
  color: @text-secondary;
  font-size: 13px;
  font-weight: 400;
}

.user-header-actions {
  display: flex;
  gap: 12px;
}

:deep(.el-button--primary) {
  --el-button-bg-color: @color-primary;
  --el-button-border-color: @color-primary;
}
</style>
