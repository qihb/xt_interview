# 用户与部门新增弹窗抽取 · 落地计划

**目标**：将用户和部门的新增、编辑弹窗分别抽取为独立 Vue 组件，保持现有页面风格、表单校验和接口行为不变。
**技术栈 / 约束**：Vue 3 `<script setup lang="ts">`、Element Plus、Less；遵循现有 feature-based 目录和管理后台设计规范。
**涉及文件**：
- 新增 `frontend/web/src/views/account/components/UserDialog.vue`
- 新增 `frontend/web/src/views/department/components/DepartmentDialog.vue`
- 修改 `frontend/web/src/views/account/UserManagement.vue`
- 修改 `frontend/web/src/views/department/DepartmentManagement.vue`
- 补充或调整对应页面测试

**关键决策**：
- 新增和编辑共用同一个业务弹窗组件，通过 `mode` 和初始数据区分标题、字段状态及提交场景。
- 子组件负责表单状态、校验和表单 UI，通过 `v-model` 控制显示并向父组件 emit 提交事件；父组件负责调用 API、提示和刷新列表。

**执行约定**：本文件仅为计划；由用户确认后按步骤执行，每完成一步勾选并记录结果。

---

## 阶段 0 · 组件边界设计
- [x] 0.1 确认新增/编辑组件的 props、emits 和表单数据边界
- [x] 0.2 确认用户与部门页面接入方式

## 阶段 1 · 抽取组件
- [x] 1.1 新增用户新增/编辑弹窗组件
- [x] 1.2 新增部门新增/编辑弹窗组件

## 阶段 2 · 页面接入与验证
- [x] 2.1 从两个管理页面移除新增/编辑弹窗内联代码并接入组件
- [x] 2.2 补充组件/页面测试并运行前端测试与构建

---

## 当前状态
- 已完成组件抽取、页面接入和验证。
- 用户与部门页面分别使用独立的新增/编辑弹窗组件，17 个单元测试和生产构建均通过。
