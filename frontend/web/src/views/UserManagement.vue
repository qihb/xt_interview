<template>
  <div class="page-container">
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

      <div class="user-filters">
        <el-input
          v-model="filters.username"
          clearable
          placeholder="请输入用户名"
          style="width: 220px"
          @keyup.enter="handleSearch"
        />
        <AccountStatusSelect v-model="filters.status" />
        <AccountRoleSelect v-model="filters.roleLevel" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
      @close="resetForm"
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
          <DepartmentSelect v-model="formData.departmentId" scope="all" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            show-password
            maxlength="20"
            show-word-limit
            :placeholder="isEdit ? '留空则不修改密码' : '留空则默认123456'"
          />
        </el-form-item>
        <el-form-item label="权限级别" prop="roleLevel">
          <el-select v-model="formData.roleLevel" placeholder="请选择权限级别" style="width: 100%">
            <el-option label="用户权限" :value="1" />
            <el-option label="管理员权限" :value="2" />
            <el-option label="超级管理员权限" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { accountApi, type Account, useAccountList } from '@/features/account'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import AppPagination from '@/components/AppPagination.vue'
import { AccountRoleSelect, AccountStatusSelect } from '@/features/account'

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

const formRef = ref<FormInstance>()

interface UserFormData {
  username: string
  name: string
  gender: string
  phone: string
  password?: string
  roleLevel: number
  status: number
  isSuper: number
  departmentId: number | null
}

const formData = reactive<UserFormData>({
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

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为 3-20 个字符', trigger: 'blur' },
    {
      pattern: /^[A-Za-z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应为 2-20 个字', trigger: 'blur' },
    {
      pattern: /^[\u4e00-\u9fa5A-Za-z·\s]+$/,
      message: '姓名只能包含中文、字母或间隔点',
      trigger: 'blur'
    }
  ],
  phone: [
    {
      pattern: /^(1[3-9]\d{9}|0\d{2,3}-?\d{7,8})$/,
      message: '请输入正确的手机号或座机号',
      trigger: 'blur'
    }
  ],
  password: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (value.length < 6 || value.length > 20) {
          callback(new Error('密码长度应为 6-20 个字符'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  roleLevel: [{ required: true, message: '请选择权限级别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

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
  formRef.value?.resetFields()
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

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = { ...formData }
      if (data.password === '') {
        delete data.password
      }

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
  })
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

.user-section {
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
  gap: 12px;
  margin-bottom: 20px;
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
