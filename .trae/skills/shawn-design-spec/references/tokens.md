# Token 详细定义

> 设计 Token 的完整定义。速查版见 SKILL.md。

---

## 1. 色彩

### 1.1 主色

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--color-primary` | `@color-primary` | #1764E8 | 主色、链接、选中态 |

主色色阶（用于 hover、active、disabled 等状态）：

| 状态 | 值 | 用途 |
|------|-----|------|
| hover | #4087F0 | 按钮 hover、链接 hover |
| active | #0F4FBF | 按钮 active |
| light | #f0f5ff | 选中行背景、tag 背景 |
| lighter | #f5f8ff | hover 背景 |
| disabled | rgba(23, 100, 232, 0.5) | 禁用态 |

### 1.2 状态色

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--color-success` | `@color-success` | #67C23A | 成功、完成、通过 |
| `--color-warning` | `@color-warning` | #E6A23C | 警告、进行中 |
| `--color-danger` | `@color-danger` | #F56C6C | 错误、删除、高优先级 |

状态色组合（Tag 类型映射）：

| 状态 | Tag type | 背景色 | 文字色 |
|------|----------|--------|--------|
| 成功 | success | #f0f9eb | #67c23a |
| 警告 | warning | #fdf6ec | #e6a23c |
| 危险 | danger | #fef0f0 | #f56c6c |
| 信息 | info | #f4f4f5 | #909399 |
| 主色 | primary | #f0f5ff | #1764e8 |

### 1.3 文字色

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--text-primary` | `@text-primary` | rgba(0,0,0,0.85) | 标题、正文 |
| `--text-secondary` | `@text-secondary` | rgba(0,0,0,0.65) | 次要文字、label |
| `--text-muted` | `@text-muted` | rgba(0,0,0,0.45) | 辅助文字、placeholder |

### 1.4 背景色

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--page-bg` | `@page-bg` | #f5f7fa | 页面背景（body） |
| `--card-bg` | `@card-bg` | #fff | 卡片、弹框背景 |
| `--table-header-bg` | `@table-header-bg` | #fafafa | 表头、标签页头 |

### 1.5 边框色

| Token | 值 | 用途 |
|-------|-----|------|
| `--divider` | rgba(0,0,0,0.06) | 分割线、卡片边框 |

---

## 2. 字体

### 2.1 字体栈

```less
@font-stack: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
             'PingFang SC', 'Microsoft YaHei', sans-serif;
```

等宽字体（编号、代码）：
```less
@font-mono: "SFMono-Regular", "SF Mono", "Menlo", "Consolas", monospace;
```

### 2.2 字号体系

| 字号 | 字重 | 行高 | Less 变量 | 用途 |
|------|------|------|----------|------|
| 12px | 400 | 20px | `@font-size-xs` | 辅助文字、表格单元格、标签 |
| 14px | 400 | 22px | `@font-size-sm` | 正文默认 |
| 14px | 500 | 22px | - | 表单区域标题、卡片标题 |
| 16px | 500 | 24px | `@font-size-md` | section-title、卡片标题 |
| 20px | 600 | 28px | `@font-size-lg` | 页面标题 |
| 24px | 700 | 32px | `@font-size-xl` | 大标题 |

### 2.3 常用文字样式

**分区标题（.section-title）**
```css
.section-title {
  position: relative;
  margin: 0 0 12px 14px;
  font-size: 16px;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.85);
}
.section-title::after {
  content: '';
  position: absolute;
  top: 50%;
  left: -12px;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background-color: #1764e8;
  border-top: 4px solid #C9D8F2;
  border-bottom: 4px solid #C9D8F2;
}
```

---

## 3. 间距

### 3.1 基础间距

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--spacing-xs` | `@spacing-xs` | 4px | 最小间距 |
| `--spacing-sm` | `@spacing-sm` | 8px | 紧凑间距 |
| `--spacing-md` | `@spacing-md` | 12px | 表单项间距 |
| `--spacing-lg` | `@spacing-lg` | 16px | 卡片内边距 |
| `--spacing-xl` | `@spacing-xl` | 20px | 页面 padding |
| `--spacing-2xl` | `@spacing-2xl` | 24px | 大间距 |
| `--spacing-3xl` | `@spacing-3xl` | 32px | 区块间距 |
| `--spacing-4xl` | `@spacing-4xl` | 48px | 大区块间距 |
| `--spacing-5xl` | `@spacing-5xl` | 64px | 页面级间距 |

### 3.2 常用布局间距

| 场景 | 值 |
|------|-----|
| 页面容器 padding | 20px |
| 卡片内边距 | 16px 20px |
| 表单区域标题 margin | 0 0 12px 14px |
| 筛选栏 padding | 12px 0 |
| 分页栏 padding-top | 12px |

### 3.3 页面布局宽度

| 断点 | 宽度 | 用途 |
|------|------|------|
| 标准 | 1440px | 主要布局宽度 |
| 紧凑 | 1280px | 侧边栏收起时 |
| 中等 | 980px | 抽屉、详情页 |
| 窄 | 720px | 弹框表单 |
| 最窄 | 480px | 小弹框 |

---

## 4. 圆角与阴影

### 4.1 圆角

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--radius-sm` | `@radius-sm` | 2px | 小元素 |
| `--radius-md` | `@radius-md` | 4px | 按钮、输入框 |
| `--radius-lg` | `@radius-lg` | 8px | 卡片、弹框 |
| `--radius-xl` | `@radius-xl` | 12px | 大卡片 |

### 4.2 阴影

| Token | Less 变量 | 值 | 用途 |
|-------|----------|-----|------|
| `--shadow-sm` | `@shadow-sm` | 0 1px 2px rgba(0,0,0,0.06) | 卡片默认 |
| `--shadow-md` | `@shadow-md` | 0 4px 12px rgba(0,0,0,0.08) | 卡片 hover |
| `--shadow-lg` | `@shadow-lg` | 0 8px 24px rgba(0,0,0,0.12) | 弹框 |
| `--shadow-xl` | `@shadow-xl` | 0 16px 48px rgba(0,0,0,0.16) | 大弹框 |

### 4.3 Hover 交互效果

| 元素 | 效果 |
|------|------|
| 卡片 | box-shadow 从 sm → md |
| 表格行 | background rgba(0,0,0,0.02) |
| 按钮 | opacity 0.85 |
| 链接 | color 变为主色 |

---

## 5. Less 变量速查

```less
/* 色彩 */
@color-primary:   #1764E8;
@color-success:   #67C23A;
@color-warning:   #E6A23C;
@color-danger:    #F56C6C;

/* 文字色 */
@text-primary:    rgba(0, 0, 0, 0.85);
@text-secondary:  rgba(0, 0, 0, 0.65);
@text-muted:      rgba(0, 0, 0, 0.45);

/* 背景 */
@page-bg:         #f5f7fa;
@card-bg:         #fff;
@table-header-bg: #fafafa;

/* 字体 */
@font-stack: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
             'PingFang SC', 'Microsoft YaHei', sans-serif;

/* 间距 */
@spacing-xs:  4px;
@spacing-sm:  8px;
@spacing-md:  12px;
@spacing-lg:  16px;
@spacing-xl:  20px;
@spacing-2xl: 24px;
@spacing-3xl: 32px;

/* 圆角 */
@radius-sm: 2px;
@radius-md: 4px;
@radius-lg: 8px;
@radius-xl: 12px;

/* 阴影 */
@shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.06);
@shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
@shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
@shadow-xl: 0 16px 48px rgba(0, 0, 0, 0.16);
```
