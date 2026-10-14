# 用户管理部门树 · 落地计划

**目标**：在用户管理页增加左侧部门树，默认选中“全部”并加载全部用户，选择部门后按部门筛选右侧用户列表。
**技术栈 / 约束**：Vue 3 + TypeScript + Element Plus + Less；按 feature-first 架构组织 API；后端 Spring Boot + JPA；部门树当前采用“全部”根节点下的一级部门节点。
**涉及文件**：
- 修改 `frontend/web/src/features/account/api/index.ts`、`composables/use-account-list.ts`
- 修改 `frontend/web/src/features/account/index.ts`（如需补充公共导出）
- 修改 `frontend/web/src/views/account/UserManagement.vue`、`UserManagement.test.ts`
- 修改后端 `AccountController`、`AccountService`、`AccountServiceImpl`、`AccountRepository`
- 补充后端 Controller / Service 测试

**关键决策**：
- 当前部门模型没有父部门字段，本次不扩展数据库；“全部”作为根节点，现有部门作为一级节点。
- 选择“全部”时不传部门筛选；选择部门时按 `departmentId` 精确筛选。
- 部门树初始化时全部展开并默认选中“全部”；切换部门重置到第一页。

**执行约定**：本文件仅为计划；由用户指定步骤后再执行，每完成一步勾选并记录验证结果。

---

## 阶段 0 · 查询链路
- [x] 0.1 为用户分页 API 增加可选 `departmentId` 参数，并贯通 Controller、Service、Repository。
- [x] 0.2 补充后端参数绑定、组合筛选和默认行为测试。已完成测试代码；当前环境执行 Maven 时因 Java 编译器不支持发行版本 26，未能运行。

## 阶段 1 · 用户管理页面
- [x] 1.1 加载部门列表并构建“全部 + 部门”树数据。
- [x] 1.2 实现默认展开、默认选中全部、部门点击联动用户列表。
- [x] 1.3 调整双栏布局和 Less 样式，保持现有筛选、分页、增删改能力。
- [x] 1.4 补充页面初始化、树选择和部门查询测试。

## 阶段 2 · 验证与交付
- [x] 2.1 运行前端相关测试与构建。组件拆分后全量测试 24 个通过，生产构建通过。
- [ ] 2.2 运行后端相关测试，更新本计划当前状态。待切换到支持 Java 26 的 JDK 后重试。

## 阶段 3 · 部门树组件化
- [x] 3.1 新增独立部门树组件，封装部门加载、树数据和节点选择。
- [x] 3.2 用户管理页接入组件，保持部门筛选行为不变。
- [x] 3.3 补充组件测试并运行前端验证。

---

## 当前状态
- 已完成前后端代码实现和前端验证；后端测试受当前终端 Java 版本限制未完成。
- 阶段 3 已完成；组件专项测试 8 个通过，前端全量测试 24 个通过，生产构建通过。
