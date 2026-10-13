# 页面模板指南

> 帮助 AI 选择合适的模板。模板文件见 templates/。

---

## 模板选择决策树

```
用户需求
    │
    ▼
是否为列表页？
    │
    ├── 是 → 需要统计卡片？
    │        ├── 是 → list-stats-tabs.html
    │        └── 否 → 需要多行筛选？
    │                 ├── 是 → list-multi-filter.html
    │                 └── 否 → list-standard.html
    │
    └── 否 → 是否为详情页？
             ├── 是 → 需要多标签页？
             │        ├── 是 → detail-tabs.html
             │        └── 否 → 需要抽屉？
             │                 ├── 是 → detail-drawer.html
             │                 └── 否 → detail-info.html
             │
             └── 否 → 是否为表单页？
                      ├── 是 → 需要弹框？
                      │        ├── 是 → dialog-form.html
                      │        └── 否 → form-page.html
                      │
                      └── 否 → 是否为仪表盘？
                               ├── 是 → dashboard.html
                               └── 否 → 回退到 Token 规范
```

---

## 模板清单

| 模板 | 文件 | 适用场景 |
|------|------|---------|
| 列表页（标准） | `assets/templates/list-standard.html` | 筛选+表格+分页 |
| 列表页（多筛选） | `assets/templates/list-multi-filter.html` | 多行筛选+多操作按钮 |
| 列表页（统计+tabs） | `assets/templates/list-stats-tabs.html` | 顶部统计+tabs切换 |
| 详情页（tabs） | `assets/templates/detail-tabs.html` | banner+多标签页 |
| 详情页（信息展示） | `assets/templates/detail-info.html` | banner+描述列表 |
| 详情页（抽屉） | `assets/templates/detail-drawer.html` | 抽屉内表单/详情 |
| 数据仪表盘 | `assets/templates/dashboard.html` | 统计卡+图表+表格 |
| 独立表单页 | `assets/templates/form-page.html` | 返回+分区表单 |
| 弹框表单 | `assets/templates/dialog-form.html` | 弹框内表单 |

---

## 无模板时的降级策略

当没有模板匹配需求时（如前台页面、复杂场景），必须遵守基础规范：
- 颜色：主色 #1764E8，文字色 rgba 方案，背景 #f5f7fa
- 字号：12/14/16/20px 四级字阶
- 间距：4/8/12/16/20/24/32px 间距体系
- 圆角：2/4/8/12px 四级圆角
- 阴影：sm/md/lg/xl 四级阴影
