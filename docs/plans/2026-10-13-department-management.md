# 任务一：部门管理模块 · 落地计划

**目标**：在现有用户管理后台中新增部门管理模块，完成部门的增删改查、软删除和前端页面接入。
**技术栈 / 约束**：Spring Boot 4.0.6、Java 26、JPA、Flyway、Vue 3、TypeScript、Element Plus；遵循 `agent.md` 的分层、鉴权、迁移和验证约定。
**涉及文件**：新增部门 Flyway 迁移脚本、`Department` 实体/DTO/Repository/Service/ServiceImpl/Controller、前端部门 API 与 `DepartmentManagement.vue`；修改路由、主布局菜单及标题映射；按需补充测试。
**关键决策**：
- 删除采用 `deleted = 1` 软删除，列表和详情默认只返回未删除部门。
- 部门编码由数据库唯一约束保证唯一；创建和更新时返回明确的重复编码错误。
- 部门状态使用 `0-禁用 / 1-启用`，初始测试数据为研发部、运维部。
- 部门接口沿用现有 JWT 保护和 `/api` 请求前缀，不扩展任务二的用户-部门关联。
**执行约定**：本文件仅为计划；由用户确认后按指定步骤执行，每完成一个阶段勾选并更新当前状态。

---

## 阶段 0 · 数据库迁移
- [x] 0.1 新增 `V2__sys_department.sql`，创建 `sys_department` 表、索引、唯一编码约束和软删除字段。
- [x] 0.2 插入研发部、运维部两条初始化数据；Flyway 已成功升级数据库到 v2。

## 阶段 1 · 后端部门模块
- [x] 1.1 新增 `Department`、`DepartmentDTO`、`DepartmentRepository`、`DepartmentService`、`DepartmentServiceImpl` 和 `DepartmentController`。
- [x] 1.2 实现部门列表、详情、创建、更新、软删除五个接口，并补充名称/编码校验和重复编码处理。
- [x] 1.3 已确认接口受 JWT 保护，响应包含部门状态和审计时间，列表/详情默认排除软删除数据。

## 阶段 2 · 前端部门管理
- [x] 2.1 在 `src/api/index.ts` 新增部门 CRUD API 封装及类型定义。
- [x] 2.2 新增 `DepartmentManagement.vue`，实现列表、状态展示、新增/编辑表单、删除确认、加载和错误提示。
- [x] 2.3 新增部门路由，并在 `MainLayout.vue` 增加“部门管理”菜单和页面标题映射；补充 `less` 开发依赖。

## 阶段 3 · 验证与交付
- [x] 3.1 使用 Java 26 执行 `mvn compile`，编译通过。
- [x] 3.2 执行 `npm run build`，构建通过；保留 Vite 的大 chunk 警告。
- [x] 3.3 使用 `admin / 123456` 在 8081 实例验证部门列表、创建、更新、删除及软删除详情不可用；前端菜单和路由已完成静态构建验证。
- [x] 3.4 完成阶段记录；删除后详情沿用现有项目异常处理，当前返回 HTTP 500，未新增统一异常映射。

---

## 当前状态
- 任务一开发完成。
- 验证命令：`JAVA_HOME="$HOME/.sdkman/candidates/java/26.0.2-amzn" PATH="$JAVA_HOME/bin:$PATH" mvn compile`、`npm run build`。
- 运行验证：Flyway v2 成功；部门 CRUD 和软删除接口验证通过。
- 已知限制：项目暂无统一异常处理器，查询已软删除部门时返回 500；可在后续统一异常治理中改为 404/业务错误码。
