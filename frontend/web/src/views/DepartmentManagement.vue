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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑部门' : '新增部门'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { departmentApi } from '@/api'
import type { Department, DepartmentPayload } from '@/api'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const departments = ref<Department[]>([])
const formRef = ref<FormInstance>()

const formData = reactive<DepartmentPayload>({
  name: '',
  code: '',
  description: '',
  status: 1
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 20, message: '部门名称长度应为 2-20 个字', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value && value.trim() !== value) {
          callback(new Error('部门名称首尾不能有空格'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' },
    { min: 2, max: 20, message: '部门编码长度应为 2-20 个字符', trigger: 'blur' },
    {
      pattern: /^[A-Za-z0-9_-]+$/,
      message: '部门编码只能包含字母、数字、下划线或短横线',
      trigger: 'blur'
    }
  ],
  description: [
    { max: 200, message: '部门描述不能超过 200 个字', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

onMounted(() => {
  loadDepartments()
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

function resetForm() {
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.status = 1
  isEdit.value = false
  editId.value = 0
  formRef.value?.resetFields()
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

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await departmentApi.update(editId.value, { ...formData })
      ElMessage.success('部门更新成功')
    } else {
      await departmentApi.create({ ...formData })
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
