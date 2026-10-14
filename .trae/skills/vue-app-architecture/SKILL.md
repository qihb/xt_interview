---
name: vue-app-architecture
description: Vue 3 管理后台的功能分层与架构规范。用于新增或重构功能、决定目录归属、设计 API 层和状态管理、处理错误与权限、审查模块边界时。触发词包括架构、分层、features、目录结构、API 层、状态管理、错误处理、权限。
---

# Vue 应用架构

本项目采用 Vue 3 + Vite + TypeScript + Element Plus + Pinia + Vue Router + Axios。架构规范保留 feature-first、单向依赖、显式边界和就近状态等核心理念；项目根目录的 `agent.md` 优先级最高，发生冲突时以它为准。

## 核心原则

按业务功能组织代码，而不是按文件类型集中堆放。页面负责组装和布局，业务逻辑、请求、类型和状态尽量封装在对应 feature 内；只有跨 feature 复用的内容才上提到公共目录。

## 目录结构

当前项目入口为 `frontend/web/src`，推荐逐步演进为：

```text
src/
  app/                  # 应用级装配：App、全局 Provider、错误兜底
  assets/               # 静态资源
  components/           # 跨 feature 复用的通用组件
  config/               # 环境变量与全局配置
  features/<feature>/   # 按业务领域划分，如 user、department、auth
    api/                # feature 请求函数与响应类型
    components/         # feature 专用组件
    composables/        # feature 专用组合式函数
    stores/             # feature 专用 Pinia store
    types/              # feature 类型
    utils/              # feature 工具函数
    index.ts            # 对外公共 API
  layouts/              # 页面布局
  router/               # Vue Router 路由与守卫
  stores/               # 跨 feature 的全局 Pinia store，如 auth
  types/                # 跨 feature 共享类型
  utils/                # 跨 feature 共享工具
  views/                # 页面视图，只做 feature 组装
```

已有项目目录可以兼容过渡，但新增复杂业务优先采用 `features/<feature>`，不要继续扩大按 `api/components/stores` 横向堆放的规模。

## 边界铁律

1. 禁止跨 feature 深层导入。外部只能从 `@/features/<feature>` 导入，内部目录通过 `index.ts` 控制公共 API。
2. feature 不得依赖 `views` 或 `layouts`；页面只能组装 feature 和公共组件。
3. feature 之间保持单向、低耦合依赖。共享能力上提到 `components/`、`composables/`、`utils/` 或 `types/`。
4. 不建立聚合全项目的大 barrel。`index.ts` 只作为单个 feature 的稳定出口。
5. 可使用 `_components/`、`_utils/` 等命名强调 feature 私有实现，外部不得绕过出口引用。

## API 层

- 全项目只使用 `src/utils/request.ts` 或 `src/api/request.ts` 中的唯一 Axios 实例和拦截器；禁止在视图、组件或 feature 内重新 `axios.create`。
- 请求函数按业务领域放入 `features/<feature>/api/`，组件不直接拼接 URL。
- 拦截器统一处理响应解包、业务错误、401 登录失效和常见 5xx 提示；业务代码只处理已经类型化的数据或异常。
- 请求入参、响应和分页结构必须显式标注 TypeScript 类型。
- API 层不依赖 Vue 组件生命周期，便于在 Pinia action、路由守卫和测试中复用。

## 状态管理

| 状态类型 | 放置位置 | 说明 |
| --- | --- | --- |
| 组件状态 | `ref` / `reactive` / `computed` | 只被单个组件使用的 UI 状态 |
| 表单状态 | Element Plus `Form` | 使用表单模型和校验规则，不复制一套无意义的受控状态 |
| 应用状态 | Pinia | 登录态、当前用户、跨路由共享的客户端状态 |
| 服务端缓存 | 就近请求或 Pinia | 明确管理加载、数据、错误和失效，不臆造 React Query API |
| URL 状态 | Vue Router query | 分页、筛选、排序等可分享、可后退的状态 |

优先使用 Setup Store；通过 `storeToRefs()` 解构响应式 state/getter；action 负责异步业务流程。状态放在最小必要范围，避免无理由全局化和多层 props 透传。

## 错误处理

- 预期错误，如表单校验失败和业务校验失败，在提交处用 Element Plus `ElMessage`、`ElMessageBox` 或 `ElAlert` 就地呈现，不把用户可处理的问题交给全局兜底。
- 非预期异常交给应用级错误页或错误边界处理。Vue 组件可使用 `onErrorCaptured`，路由可配置 `errorComponent`、导航守卫异常和统一路由错误处理。
- `App.vue` 或应用装配层提供全局兜底；路由级页面提供局部错误状态，避免单个页面异常拖垮整站。
- 网络错误的分类、401 跳转登录和 5xx 提示由 Axios 拦截器统一处理，避免各组件重复判断。

## 安全与权限

- 受保护请求统一使用 `Authorization: Bearer <token>`。Token 由 Pinia 管理并按项目约定持久化；持久化前评估 XSS 风险，禁止把敏感凭证写入日志。
- 默认依赖 Vue 模板转义，禁止对不可信内容使用 `v-html`；确需富文本时必须先净化。
- 敏感操作的最终权限以服务端鉴权为准，前端权限只用于隐藏、禁用和路由体验。
- 采用权限点模型优于散落的角色判断。集中定义权限点，使用统一的 `useAuthorization` 或 `v-permission` 指令。
- 路由权限通过 `meta` 和集中式导航守卫处理，新增受保护页面必须同步菜单和路由元信息。

## Vue 项目规范

- 使用 Vue 3 Composition API 和 `<script setup lang="ts">`，组件用 PascalCase，文件和目录使用 kebab-case。
- 使用 `@` 指向 `src`；如果别名未配置，先补齐 Vite 与 TypeScript 配置再使用。
- 页面放在 `views/`，业务模块放在 `features/`；不要在页面中实现完整请求、权限和复杂状态流程。
- Element Plus 负责交互组件和表单，项目约定的 Less 或公共样式负责页面视觉；遵循现有主题，不随意覆盖组件内部 class。
- 导入顺序：Vue 内置 → 第三方依赖 → `@/` 别名 → 相对路径；同组按字母序整理。
- 不引入重复的请求客户端、全局事件总线或未经约定的状态库。

## 测试

- 单元和组件测试使用 Vitest + Vue Test Utils，测试文件与源码就近放置，命名为 `*.test.ts` 或 `*.test.tsx`。
- API 测试使用 MSW 拦截网络请求，避免依赖真实后端。
- 端到端测试使用 Playwright，覆盖登录、权限、核心 CRUD 和错误状态。
- 先通过测试锁定行为，再实现功能；测试应验证用户可见行为和模块边界，而不是内部实现细节。

## 性能

- 路由页面使用 Vue Router 的懒加载，避免首屏加载所有业务模块。
- 状态放在最小需要它的层级，优先使用 `computed`、`shallowRef` 和稳定的组件边界减少无谓更新。
- 列表分页、筛选和排序优先放 URL query，确保刷新、分享和浏览器后退行为一致。
- 复杂交互通过组件组合和 slots 扩展，避免大量布尔 props 和单组件过度膨胀。

## 新增 feature 检查清单

- [ ] 在 `frontend/web/src/features/<name>/` 下建立按需目录。
- [ ] 提供 `index.ts`，只导出稳定的公共 API。
- [ ] 页面放在 `views/`，只负责布局和 feature 组合。
- [ ] 请求走统一 Axios 实例，组件不直接拼 URL。
- [ ] 分页、筛选、排序等可分享状态放入 Vue Router query。
- [ ] Pinia store 只承载跨组件或跨路由状态，并明确 loading/error/失效处理。
- [ ] 路由注册在 `router/`，配置懒加载、`meta` 权限和错误处理。
- [ ] 使用 Vitest/Vue Test Utils、MSW 或 Playwright 覆盖关键行为。
- [ ] 执行项目约定的 `npm run build`，并按现有 lint/typecheck 命令完成检查。
