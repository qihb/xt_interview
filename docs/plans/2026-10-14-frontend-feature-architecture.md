# 前端 Feature 架构优化 · 落地计划

**目标**：依据 `vue-app-architecture` 技能，将当前前端按业务领域拆分为 `auth`、`account`、`department` feature，降低页面对 API 和内部组件的直接依赖，并同步更新根目录 `agent.md` 的前端开发约定。
**技术栈 / 约束**：Vue 3 Composition API、Vite 5、TypeScript、Element Plus、Pinia、Vue Router、Axios、Vitest；保持现有路由、接口路径、登录流程和页面行为兼容。
**涉及文件**：
- 新增 `frontend/web/src/features/auth/`
- 新增 `frontend/web/src/features/account/`
- 新增 `frontend/web/src/features/department/`
- 调整 `views/`、`layouts/`、`components/`、`stores/`、`api/` 及相关测试导入
- 更新根目录 `agent.md` 第 3、7、9、10 节
- 保留 `src/utils/request.ts` 作为唯一 Axios 实例
**关键决策**：
- 采用渐进式迁移：先移动领域 API、类型、账号专属组件和认证 store，再将页面业务流程提取到 feature composables，避免一次性改动路由和 UI。
- `src/api/index.ts` 不再作为业务 API 聚合出口；测试和业务代码改从对应 feature 的 `index.ts` 引入。
- `src/components/` 只保留跨领域组件，`DepartmentSelect` 与 `AppPagination` 继续作为公共组件。

---

## 阶段 0 · Feature 边界设计
- [x] 0.1 建立 `auth`、`account`、`department` 的目录和公共出口。
- [x] 0.2 将领域类型与 API 拆分到对应 feature，保留统一 request 实例。
- [x] 0.3 将认证 store 归入 `features/auth/stores`，将账号筛选组件归入 `features/account/components`。

## 阶段 1 · 页面依赖收敛
- [x] 1.1 为用户管理和部门管理提取 feature composables，页面只负责布局和事件组合。
- [x] 1.2 更新登录页、主布局、公共组件和路由相关导入，禁止跨 feature 深层导入。
- [x] 1.3 同步调整测试 mock 与导入路径，保持测试行为不变。

## 阶段 2 · 项目规范同步与验证
- [x] 2.1 更新 `agent.md` 的前端目录结构、分层、API、状态、错误处理和测试约定。
- [x] 2.2 清理废弃的根级业务 API 与旧模块目录。
- [x] 2.3 执行 `npm test`、`npm run build`，并检查 `rg` 确认没有旧路径和跨 feature 深层导入。

---

## 当前状态
- 计划已完成。
- `npm test -- --run`：8 个测试文件、17 个测试通过。
- `npm run build`：构建通过；Vite 仍提示现有主 chunk 超过 500 kB。
- `npx vue-tsc --noEmit`：受既有 `src/utils/request.test.ts` Axios 拦截器测试类型问题影响，未通过。
