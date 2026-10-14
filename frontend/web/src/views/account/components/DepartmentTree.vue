<template>
  <aside class="department-panel">
    <div class="department-panel-title">部门</div>
    <el-tree
      v-loading="loading"
      class="department-tree"
      :data="treeData"
      node-key="id"
      highlight-current
      current-node-key="all"
      :default-expanded-keys="['all']"
      :props="{ label: 'label', children: 'children' }"
      @node-click="handleNodeClick"
    />
  </aside>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useDepartmentList, type Department } from '@/features/department'

interface DepartmentTreeNode {
  id: string | number
  label: string
  departmentId?: number
  children?: DepartmentTreeNode[]
}

const emit = defineEmits<{
  select: [departmentId: number | undefined]
}>()

const { loading, departments, loadDepartments } = useDepartmentList()
const treeData = ref<DepartmentTreeNode[]>([
  { id: 'all', label: '全部', children: [] }
])

function buildTree(items: Department[]) {
  treeData.value = [{
    id: 'all',
    label: '全部',
    children: items.map((department) => ({
      id: department.id,
      label: department.name,
      departmentId: department.id
    }))
  }]
}

function handleNodeClick(node: DepartmentTreeNode) {
  emit('select', node.id === 'all' ? undefined : node.departmentId)
}

onMounted(async () => {
  await loadDepartments()
  buildTree(departments.value)
})
</script>

<style lang="less" scoped>
@text-primary: rgba(0, 0, 0, 0.85);

.department-panel {
  min-height: 520px;
  padding: 16px 12px;
  background: #fff;
  border-radius: 8px;
}

.department-panel-title {
  padding: 0 8px 12px;
  color: @text-primary;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
}

.department-tree {
  margin-top: 12px;

  :deep(.el-tree-node__content) {
    height: 36px;
  }
}
</style>
