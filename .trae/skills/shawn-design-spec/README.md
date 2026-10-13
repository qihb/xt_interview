# shawn-design-spec

B2B 管理后台设计规范 + 代码规范。紧凑、务实、信息密集型风格。

## 这是什么

为 AI 和开发者提供统一视觉规范和代码规范的工程。基于 Element Plus v2.8.5，保证生成的页面风格一致、代码结构统一。

**不是组件库，是设计规范。** 不需要安装依赖，只需要遵循规范中定义的 Token、间距、字号、颜色、交互模式和代码规范。

## 安装

```bash
# 全局安装（所有项目可用）
npx shawn-design-spec install

# 本地安装（仅当前项目）
npx shawn-design-spec install --local
```

安装后，skill 会被复制到 `~/.agents/skills/shawn-design-spec/`（全局）或 `.agents/skills/shawn-design-spec/`（本地），ZCode 会自动识别。

## 使用方式

### AI 开发时参考

AI 在生成 B2B 后台页面时会自动触发此 skill，按照以下流程工作：

1. 读 `SKILL.md` 了解硬规则和工作流程
2. 根据任务查 `references/templates.md` 选择模板
3. 按需读取 `references/` 下的详细规范文件
4. 参考 `assets/templates/` 和 `assets/preview/` 生成代码

### 开发者手动参考

1. 打开 `index.html`，浏览「主题」和「组件」标签页，了解视觉规范
2. 查阅 `references/` 下的规范文件
3. 将 CSS 变量复制到项目全局样式中

## 核心文件

| 文件 | 作用 |
|------|------|
| `SKILL.md` | 入口文件：硬规则 + Token 速查 + 工作流程 + 文件索引 |
| `references/tokens.md` | Token 详细定义（色阶、字体行高、间距场景等） |
| `references/components.md` | Element Plus 组件规范（表格/表单/弹框等） |
| `references/templates.md` | 页面模板使用指南（决策树 + 何时用哪个模板） |
| `references/code-standards.md` | Vue/API/SQL 代码规范 |
| `assets/templates/*.html` | 9 个页面模板（浏览器打开预览） |
| `assets/preview/*.html` | 34 个组件预览（浏览器打开预览） |
| `index.html` | 可视化导航入口 |

## 三层规范

### 第一层：基础 Token

| 类别 | 内容 | 详细定义 |
|------|------|---------|
| 色彩 | 主色 #1764E8 + 状态色 + 文字色 rgba 方案 | `references/tokens.md` |
| 字体 | 12-24px 字阶 + 系统字体栈 | `references/tokens.md` |
| 间距 | 4-64px 间距体系 + 常用布局间距 | `references/tokens.md` |
| 圆角 & 阴影 | 2/4/8/12px 圆角 + 4 级阴影 | `references/tokens.md` |

### 第二层：组件规范

| 分类 | 组件 | 详细规范 |
|------|------|---------|
| 输入 | Input / Select / Checkbox / Switch / Slider / Cascader / TimePicker / Segmented | `references/components.md` |
| 展示 | Table / Icon / Avatar / Badge / Progress / StatusTag / Descriptions | `references/components.md` |
| 反馈 | Alert / Message / Modal / Drawer / Tooltip / Dropdown | `references/components.md` |
| 导航 | Breadcrumb / Anchor / Pagination / Steps / Tabs | `references/components.md` |
| 布局 | TopNav / Sidenav / PageHeader / Form | `references/components.md` |

### 第三层：页面布局参考

| 模板 | 文件 | 适用场景 |
|------|------|---------|
| 列表页（标准） | `assets/templates/list-standard.html` | 筛选+表格+分页 |
| 列表页（多筛选多按钮） | `assets/templates/list-multi-filter.html` | 多行筛选+多操作按钮 |
| 列表页（统计+tabs） | `assets/templates/list-stats-tabs.html` | 顶部统计+tabs切换 |
| 详情页（tabs） | `assets/templates/detail-tabs.html` | banner+多标签页 |
| 详情页（信息展示） | `assets/templates/detail-info.html` | banner+描述列表 |
| 详情页（抽屉） | `assets/templates/detail-drawer.html` | 抽屉内表单/详情 |
| 数据仪表盘 | `assets/templates/dashboard.html` | 统计卡+图表+表格 |
| 独立表单页 | `assets/templates/form-page.html` | 返回+分区表单 |
| 弹框表单 | `assets/templates/dialog-form.html` | 弹框内表单 |

## 文件结构

```
shawn-design-spec/
├── SKILL.md                    # 入口文件
├── references/                 # 详细规范文件
│   ├── tokens.md               # Token 详细定义
│   ├── components.md           # 组件规范
│   ├── templates.md            # 模板指南
│   └── code-standards.md       # 代码规范
├── assets/                     # 静态资源
│   ├── templates/              # 页面模板（9 个）
│   └── preview/                # 组件预览（34 个）
├── bin/
│   └── install.js              # 安装脚本
├── package.json                # npm 包配置
├── index.html                  # 可视化导航
└── README.md                   # 本文件
```

## 许可

MIT
