# 任务二：用户-部门关联 · 落地计划

**目标**：为用户增加可选所属部门，打通数据库、后端 Account CRUD 与前端用户管理页，使用户列表可展示部门名称，添加/编辑可选择部门。
**技术栈 / 约束**：Spring Boot 4.0.6、Java 26、JPA/Hibernate、Flyway、Vue 3.4、TypeScript、Element Plus、Pinia；遵循 `agent.md` 与现有用户/部门页面风格。
**涉及文件**：新增 Flyway 迁移脚本；修改 Account 实体、DTO、Service 实现；新增全局部门选择组件；修改前端 API 类型与 `UserManagement.vue`；同步补充后端及前端测试。
**关键决策**：
- `sys_account.department_id` 使用 `BIGINT NULL`，外键关联 `sys_department.id`，并增加索引。
- Account 使用 JPA `@ManyToOne` 关联 Department；AccountDTO 同时提供 `departmentId` 与只读展示字段 `departmentName`，写入时仅使用 `departmentId`。
- 部门字段为非必填；未选择部门时传 `null`。部门选择范围由组件参数配置：默认仅展示 `deleted = 0 且 status = 1` 的有效部门；需要历史数据回显的业务可切换为展示全部未删除部门。部门名称由账户接口返回，避免前端额外逐行查询。
- 部门存在未删除用户关联时禁止删除，并返回明确的业务提示；无关联部门仍沿用现有软删除逻辑。
- 部门被软删除后，已有用户仍保留关联；用户列表可继续展示其部门名称，新增/编辑下拉不再提供该部门。
- 部门选择封装为全局 `DepartmentSelect` 组件，组件内部负责加载部门选项，并通过 `v-model` 输出可空的部门 ID；通过 `scope` 参数控制展示范围（`active` / `all`），业务页面不重复实现部门加载和下拉渲染。
- 部门关联检查使用 `existsByDepartmentIdAndDeleted` 这类存在性查询，不加载全部用户。
- 关联用户导致部门删除失败时返回 HTTP `409 Conflict`，前端展示后端返回的明确业务提示。
- 外键约束明确使用 `ON DELETE RESTRICT`；应用层负责业务校验，数据库层负责最终引用完整性。
- 部门删除检查与软删除操作放在同一事务中，降低并发操作下的数据不一致风险。
- `DepartmentSelect` 统一支持 `v-model`、`scope`、`clearable`、`disabled`、`placeholder` 和加载状态。

**执行约定**：本文件仅为计划；由用户确认后，再按用户指定阶段执行。每完成一个阶段，更新勾选状态、当前状态和验证结果。

---

## 阶段 0 · 数据库与后端模型
- [x] 0.1 新增 `V3__account_department.sql`，为 `sys_account` 增加可空 `department_id`、索引及 `ON DELETE RESTRICT` 外键约束。
- [x] 0.2 扩展 `Account` 实体与 `AccountDTO`，保持字段命名、时间字段和软删除结构一致。
- [x] 0.3 为 `AccountRepository` 增加部门关联存在性查询，避免为删除校验加载全部用户。
- [x] 0.4 调整 `AccountServiceImpl` 的创建、更新、查询映射：校验部门是否存在且未删除；清空部门时允许写入 `null`；返回部门 ID 和名称。
- [x] 0.5 调整 `DepartmentServiceImpl` 的删除流程：在同一事务中检查未删除关联用户，存在时抛出可映射为 `409 Conflict` 的业务异常，否则执行软删除。
- [x] 0.6 补充/调整账户与部门服务及控制器测试，覆盖无部门、有部门、更新部门、清空部门、无效部门 ID、关联用户时禁止删除部门和错误状态码。

## 阶段 1 · 前端用户管理
- [x] 1.1 为账户 API 增加 Account 类型及部门字段类型，保留现有 CRUD 请求路径。
- [x] 1.2 新增并注册全局 `DepartmentSelect` 组件，支持 `v-model`、可空值、`scope`、`clearable`、`disabled`、`placeholder` 和加载状态。
- [x] 1.3 在 `UserManagement.vue` 使用全局部门选择组件，增加“所属部门”表单项，部门为可选字段。
- [x] 1.4 在用户表格增加“所属部门”列，展示 `departmentName`，无部门显示统一占位文本。
- [x] 1.5 编辑时回填 `departmentId`，新增/重置时清空部门，提交时正确传递可空值。
- [x] 1.6 补充全局组件、前端 API 与管理页测试，验证部门加载范围、组件双向绑定、禁用部门历史回显、表单回填、账户 payload 和删除失败提示。

## 阶段 2 · 联调与回归验证
- [x] 2.1 执行后端测试与构建，确认 Flyway 迁移脚本与 JPA 映射代码已落地；实际数据库迁移需在已创建的 `xt_interview` 数据库上启动后端时验证。
- [x] 2.2 执行前端单元测试与生产构建。
- [x] 2.3 联调用户新增、编辑、清空部门、列表展示、关联用户时删除返回 `409`、无关联部门的软删除行为，以及禁用部门的历史回显。
- [x] 2.4 更新本计划执行记录，整理变更文件与验证结果。

---

## 当前状态
- 实现已完成。
- 已确认新增约束：存在未删除关联用户的部门禁止删除。
- 已确认前端部门选择需要封装为全局可复用组件。
- 已确认部门选择范围需要参数化：默认有效部门，可按业务需要展示全部未删除部门。
- 已确认纳入查询优化、`409 Conflict`、外键限制、事务一致性及完整测试覆盖要求。
- 后端测试：Java 26 下使用 `mvn test -q -Djacoco.skip=true` 通过；直接执行 JaCoCo 时受现有 JaCoCo 0.8.12 不支持 Java 26 class major version 70 影响。
- 前端测试：15 项通过；前端生产构建通过。
