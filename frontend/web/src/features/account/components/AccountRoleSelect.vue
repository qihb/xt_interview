<script setup lang="ts">
const props = withDefaults(defineProps<{
  modelValue?: number
  clearable?: boolean
  placeholder?: string
}>(), {
  clearable: true,
  placeholder: '全部权限'
})

defineEmits<{
  'update:modelValue': [value: number | undefined]
}>()
</script>

<template>
  <el-select
    :model-value="modelValue"
    :clearable="props.clearable"
    :placeholder="props.placeholder"
    style="width: 180px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-option label="用户权限" :value="1" />
    <el-option label="管理员权限" :value="2" />
    <el-option label="超级管理员权限" :value="3" />
  </el-select>
</template>
