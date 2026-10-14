<script setup lang="ts">
const props = defineProps<{
  currentPage: number
  pageSize: number
  total: number
}>()

const emit = defineEmits<{
  'page-change': [page: number]
  'size-change': [size: number]
}>()

function handlePageChange(page: number) {
  emit('page-change', page - 1)
}

function handleSizeChange(size: number) {
  emit('size-change', size)
}
</script>

<template>
  <el-pagination
    :current-page="props.currentPage + 1"
    :page-size="props.pageSize"
    :total="props.total"
    layout="total, sizes, prev, pager, next, jumper"
    :page-sizes="[10, 20, 50, 100]"
    background
    @current-change="handlePageChange"
    @size-change="handleSizeChange"
  />
</template>
