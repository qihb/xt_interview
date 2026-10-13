# 组件规范

> Element Plus 组件使用规范。速查版见 SKILL.md。

---

## 1. 表格

| 参数 | 值 | 说明 |
|------|-----|------|
| 行高（紧凑） | 40px | font-size: 12px |
| 行高（标准） | 48px | font-size: 14px |
| 表头背景 | #fafafa | font-weight: 500 |
| 边框色 | rgba(0,0,0,0.06) | border-color |
| 操作列 | link 按钮 | 无 icon，用 el-divider 分隔 |
| 选择列 | type="selection" | width="46", fixed="left" |

→ 预览：`assets/preview/component-table.html`

---

## 2. 表单

| 参数 | 值 | 说明 |
|------|-----|------|
| label-width | 100px-120px | 根据字段数量调整 |
| label-position | right | 默认右对齐 |
| 表单项间距 | 16px（标准）/ 12px（紧凑） | margin-bottom |
| 输入框宽度 | max-width 480px | 限制最大宽度 |
| 按钮区 | border-top 分割 | padding-top 16px, text-align right |

→ 预览：`assets/preview/component-form.html`

---

## 3. 弹框与抽屉

### Dialog

| 参数 | 值 |
|------|-----|
| 标准宽度 | 720px |
| 简单宽度 | 480px |
| 中等宽度 | 640px |
| header | padding 16px 20px, border-bottom |
| body | padding 16px 20px |
| footer | padding-top 12px, text-align right |

### Drawer

| 参数 | 值 |
|------|-----|
| 快速预览 | 640px |
| 完整详情 | 980px |
| header | padding 16px 20px, border-bottom |
| body | padding 16px 20px |
| footer | padding-top 12px, text-align right |

---

## 4. 分页

| 参数 | 值 | 说明 |
|------|-----|------|
| 默认 pageSize | 50 | 默认每页50条 |
| 选项 | 20/50/100/200/500 | page-sizes |
| 布局 | sizes, prev, pager, next, jumper | layout 属性 |
| 字号 | 12px | --el-pagination-font-size |

---

## 5. 其他组件

| 组件 | 预览文件 |
|------|---------|
| Input | `assets/preview/component-input.html` |
| Select | `assets/preview/component-select.html` |
| Checkbox | `assets/preview/component-checkbox.html` |
| Switch | `assets/preview/component-switch.html` |
| Tag | `assets/preview/component-status-tag.html` |
| Descriptions | `assets/preview/component-descriptions.html` |
| Breadcrumb | `assets/preview/component-breadcrumb.html` |
| Tabs | `assets/preview/component-tabs.html` |
| Tooltip | `assets/preview/component-tooltip.html` |
| Dropdown | `assets/preview/component-dropdown.html` |
