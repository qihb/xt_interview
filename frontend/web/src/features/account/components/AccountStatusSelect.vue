<script setup lang="ts">
const props = withDefaults(defineProps<{
  modelValue?: number
  clearable?: boolean
  placeholder?: string
}>(), {
  clearable: true,
  placeholder: '全部状态'
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
    style="width: 160px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-option label="启用" :value="1" />
    <el-option label="禁用" :value="0" />
  </el-select>
</template>
