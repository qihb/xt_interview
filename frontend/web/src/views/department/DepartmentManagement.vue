<template>
  <div class="page-container">
    <section class="department-section">
      <div class="department-header">
        <div class="department-header-title">
          <span>部门列表</span>
          <span class="department-count">共 {{ departments.length }} 个部门</span>
        </div>
        <div class="department-header-actions">
          <el-button :icon="Refresh" @click="loadDepartments">刷新</el-button>
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">
            新增部门
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="departments"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="部门名称" min-width="160" />
        <el-table-column prop="code" label="部门编码" width="140" />
        <el-table-column prop="description" label="部门描述" min-width="260" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
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
    </section>

    <DepartmentDialog
      v-model="dialogVisible"
      :mode="isEdit ? 'edit' : 'create'"
      :initial-data="formData"
      :submitting="submitting"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh } from '@element-plus/icons-vue'
import {
  departmentApi,
  type Department,
  type DepartmentPayload,
  useDepartmentList
} from '@/features/department'
import DepartmentDialog from './components/DepartmentDialog.vue'

const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const { loading, departments, loadDepartments } = useDepartmentList()

const formData = reactive<DepartmentPayload>({
  name: '',
  code: '',
  description: '',
  status: 1
})

onMounted(() => {
  loadDepartments()
})

function resetForm() {
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.status = 1
  isEdit.value = false
  editId.value = 0
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: Department) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  formData.name = row.name
  formData.code = row.code
  formData.description = row.description || ''
  formData.status = row.status
  dialogVisible.value = true
}

async function handleSubmit(data: DepartmentPayload) {
  submitting.value = true
  try {
    if (isEdit.value) {
      await departmentApi.update(editId.value, data)
      ElMessage.success('部门更新成功')
    } else {
      await departmentApi.create(data)
      ElMessage.success('部门创建成功')
    }
    dialogVisible.value = false
    await loadDepartments()
  } catch (error: any) {
    ElMessage.error(error.message || '保存部门失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: Department) {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门“${row.name}”吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await departmentApi.delete(row.id)
    ElMessage.success('部门删除成功')
    await loadDepartments()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除部门失败')
    }
  }
}
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

.department-section {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

.department-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.department-header-title {
  display: flex;
  align-items: baseline;
  gap: 12px;
  color: @text-primary;
  font-size: 18px;
  font-weight: 600;
}

.department-count {
  color: @text-secondary;
  font-size: 13px;
  font-weight: 400;
}

.department-header-actions {
  display: flex;
  gap: 12px;
}

:deep(.el-button--primary) {
  --el-button-bg-color: @color-primary;
  --el-button-border-color: @color-primary;
}
</style>
