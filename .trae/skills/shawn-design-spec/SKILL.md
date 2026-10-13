---
name: shawn-design-spec
description: "B2B 后台设计规范 + 代码规范。基于 Element Plus，确保页面风格统一、代码结构一致。当用户生成 B2B 后台页面、表单、列表、详情页时自动触发。"
---

# shawn-design-spec

> B2B 管理后台设计规范。紧凑、务实、信息密集型风格。
> 基于 Element Plus v2.8.5。

---

## 硬规则（不可违反）

1. **禁止** Tailwind CSS、Lucide/Heroicons 图标、CDN CSS 框架
2. **使用** Element Plus 组件（el-table、el-form、el-dialog 等）
3. **样式语言**: Less（`<style lang="less" scoped>`），变量在 style 块顶部声明
4. **字体**: 系统字体栈，不引入外部字体
5. **主色**: #1764E8 | 成功: #67C23A | 警告: #E6A23C | 危险: #F56C6C
6. **分页**: 默认 50 条，选项 20/50/100/200/500
7. **中文**: 所有 UI 文字使用中文，分页和日期选择器使用中文 locale
8. **页面背景**: body 使用 `background: #f5f7fa`

---

## 设计 Token 速查

```less
// 色彩
@color-primary:   #1764E8;
@color-success:   #67C23A;
@color-warning:   #E6A23C;
@color-danger:    #F56C6C;

// 文字色
@text-primary:    rgba(0, 0, 0, 0.85);
@text-secondary:  rgba(0, 0, 0, 0.65);
@text-muted:      rgba(0, 0, 0, 0.45);

// 背景
@page-bg:         #f5f7fa;
@card-bg:         #fff;
@table-header-bg: #fafafa;

// 字体
@font-stack: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
             'PingFang SC', 'Microsoft YaHei', sans-serif;
```

> 完整 Token 定义 → `references/tokens.md`

---

## AI 工作流程

### Step 1: 理解需求
判断页面类型（列表/详情/表单/仪表盘/复杂场景等）。

### Step 2: 查找模板
查看 `references/templates.md` 中的决策树，选择最接近的模板。
- **相似度 > 70%**：直接使用 `assets/templates/` 中对应模板
- **相似度 < 70%**：列出候选模板，主动询问用户选择

### Step 3: 选择组件
根据实际字段选择组件。→ `references/components.md`

### Step 4: 回退到主题 Token
**当模板和组件都不足以覆盖需求时（复杂场景、前台页面等），必须遵守基础规范：**
- 颜色：主色 #1764E8，文字色 rgba 方案，背景 #f5f7fa
- 字号：12/14/16/20px 四级字阶
- 间距：4/8/12/16/20/24/32px 间距体系
- 圆角：2/4/8/12px 四级圆角
- 阴影：sm/md/lg/xl 四级阴影

### Step 5: 参考预览
查看 `assets/preview/` 下对应的组件预览文件。

### Step 6: 生成代码
使用 Less 语法，遵循 `references/code-standards.md` 中的 Vue 代码规范。
涉及 API/SQL 时，遵循 `references/code-standards.md` 中对应章节。

---

## 文件索引

| 文件 | 用途 | 何时读取 |
|------|------|---------|
| `references/tokens.md` | Token 详细定义 | 需要色阶、字体行高、间距场景等详细信息时 |
| `references/components.md` | 组件规范 | 生成表格/表单/弹框等组件时 |
| `references/templates.md` | 模板指南 | 选择页面模板或布局不确定时 |
| `references/code-standards.md` | 代码规范 | 生成 Vue/API/SQL 代码时 |
| `assets/templates/*.html` | 页面模板 | 参考布局思路 |
| `assets/preview/*.html` | 组件预览 | 参考组件用法和样式覆盖 |
