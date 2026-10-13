# 代码规范

> Vue/API/SQL 代码规范，确保代码结构一致。

---

## 1. Vue 规范

### Composition API 结构顺序

`<script setup>` 内必须按以下顺序排列：

1. Import 区域（未引入的组件及时删除）
2. Props / Emits 定义
3. 生命周期 hooks（onMounted 等）
4. 组合式业务逻辑（按功能分块，用注释分隔）

### CSS/Less 命名规范

- 顶层容器：`.page-container`
- 每个逻辑区块：`<section class="xxx-section">`
- 内部结构：`.xxx-header` / `.xxx-header-title` / `.xxx-content` / `.xxx-content-list` / `.xxx-content-item`
- 所有类名全小写，kebab-case
- 样式必须用根类名包裹（Less 嵌套）
- 必须开启 `<style lang="less" scoped>`

### 代码风格约束

- 禁止提交 `console.log`、`debugger`
- 优先使用 `const`，仅在需要重新赋值时用 `let`
- 异步操作必须处理 loading/error 状态

### 异步操作标准

```javascript
// 方式1：async/await + try/catch/finally
const fetchData = async () => {
  loading.value = true
  try {
    const res = await api.get(...)
    data.value = res.data
  } catch (err) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 方式2：mixAxios 链式调用
const fetchData = () => {
  loading.value = true
  mixAxios.get(...)
    .then((response) => { data.value = response.data })
    .finally(() => { loading.value = false })
}
```

---

## 2. API 规范

### 响应格式

成功：`{ data: { ... } }`
失败：`{ code: 0, data: { message: "..." } }`

### 其他规定

- 后台接口命名需增加 `admin` 前缀
- 查询数据时，默认不返回 `deleted_at` 非空的记录
- 多表操作时，使用事务处理

---

## 3. SQL 规范

### 命名规范

- 表名/字段名：小写字母 + 下划线分隔
- 布尔字段：`is_` 前缀
- 时间字段：`_at` 后缀
- 状态字段：`_status` 后缀

### 通用字段

每张表必须包含：`id`（INT, 自增主键）、`created_at`（TIMESTAMP）、`updated_at`（TIMESTAMP）

### 数据类型

| 场景 | MySQL | 达梦 (DM) |
|------|-------|-----------|
| 状态/布尔 | TINYINT | NUMBER(1) |
| 时间 | TIMESTAMP | TIMESTAMP |

### 反模式

| 反模式 | 正确做法 |
|--------|---------|
| 字段名用驼峰 | 统一用下划线分隔 |
| 缺少 COMMENT | 每个字段必须加注释 |
| 时间字段用 DATETIME | 统一用 TIMESTAMP |
| 缺少 created_at/updated_at | 每张表必须包含 |
