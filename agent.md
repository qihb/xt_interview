# XT Interview 项目协作指南

## 1. 项目定位

这是一个前后端分离的全栈管理后台项目，当前核心业务为登录认证和用户账号管理，并计划扩展部门管理、用户-部门关联以及用户列表搜索分页。

## 2. 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Spring Boot 4.0.6、Java 26、Spring Data JPA/Hibernate、Flyway、JWT |
| 前端 | Vue 3.4、Vite 5、TypeScript、Element Plus、Pinia、Vue Router、Axios |
| 数据库 | MySQL 8.0，`utf8mb4` / `utf8mb4_unicode_ci` |
| 构建 | Maven 3.6+、npm |

## 3. 目录结构

```text
.
├── src/main/java/com/xt/interview
│   ├── controller/       # REST 控制器
│   ├── service/          # 业务接口与实现
│   ├── repository/       # Spring Data JPA 数据访问
│   ├── entity/           # 数据库实体
│   ├── dto/              # 接口数据传输对象
│   ├── filter/            # JWT 等请求过滤器
│   ├── config/            # Web / 应用配置
│   └── util/              # JWT、密码等工具类
├── src/main/resources
│   ├── application*.properties
│   └── db/migration/     # Flyway 版本化 SQL
├── frontend/web
│   ├── src/features/      # 按业务领域组织 API、类型、组件、composables 和 store
│   │   ├── auth/          # 登录认证与用户会话
│   │   ├── account/       # 用户账号管理
│   │   └── department/    # 部门管理
│   ├── src/api/           # 仅保留兼容迁移中的 API 测试或全局 API
│   ├── src/components/    # 跨 feature 复用组件
│   ├── src/layouts/       # 登录后的页面布局
│   ├── src/router/        # Vue Router
│   ├── src/stores/        # Pinia 状态
│   ├── src/views/         # 页面视图
│   └── src/utils/         # request 等跨 feature 工具
├── pom.xml
└── INTERVIEW_TASKS.md
```

## 4. 本地环境

- Java 必须使用 **26**。
- Maven 版本要求为 **3.6+**。
- Node.js/npm 应满足 Vite 5 的运行要求。
- 启动后端前，必须先手动创建数据库：

```sql
CREATE DATABASE xt_interview
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

默认开发数据库配置为 `localhost:3306`、用户名 `root`、密码 `root`。数据库本身不会由 Flyway 自动创建；Flyway 只负责执行迁移脚本和建表。

## 5. 启动与验证

### 后端

在项目根目录执行：

```bash
mvn compile
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`。启动时会执行 `src/main/resources/db/migration/` 中的 Flyway 脚本。

### 前端

```bash
cd frontend/web
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，Vite 会将 `/api` 请求代理到 `http://localhost:8080`。生产构建使用：

```bash
npm run build
```

### 默认账号

```text
用户名：admin
密码：123456
```

## 6. 后端开发约定

1. 新增业务模块时，按 `entity -> repository -> service -> service/impl -> controller -> dto` 的职责边界组织代码。
2. 数据库结构变更必须新增 Flyway 迁移脚本，文件名遵循 `V{版本}__{说明}.sql`，不要修改已执行的历史迁移。
3. JPA 自动建表已禁用，`spring.jpa.hibernate.ddl-auto=none`；表结构以 Flyway 为准。
4. 查询默认排除软删除数据（例如 `deleted = 0`）；删除操作优先采用项目已有的软删除模式。
5. 接口输入使用 DTO 和校验注解，避免直接暴露实体作为写入参数。
6. 受保护接口依赖 `Authorization: Bearer <JWT>`；认证逻辑集中在 `JwtUtil` 和 `JwtAuthenticationFilter`。
7. 密码处理须沿用现有密码摘要逻辑。不要在日志、响应或异常中输出明文密码和 JWT。
8. 新增接口时同步更新前端 API 封装、路由和页面状态处理。

## 7. 前端开发约定

1. 使用 Vue 3 Composition API、`<script setup lang="ts">` 和 TypeScript，页面放在 `frontend/web/src/views`。
2. 新增复杂业务按 `features/<feature>` 组织，领域内按 `api`、`components`、`composables`、`stores`、`types` 和 `utils` 分层。
3. 页面只负责布局与 feature 组合；请求、业务状态和可复用流程放在 feature 内，禁止在视图中实现完整 CRUD 流程。
4. feature 对外只通过自己的 `index.ts` 暴露公共 API，禁止跨 feature 深层导入；feature 不得依赖 `views` 或 `layouts`。
5. 全项目只使用 `src/utils/request.ts` 的 Axios 实例；API 函数按领域放在对应 feature 的 `api/`，不在组件中拼接 URL 或创建 Axios 实例。
6. 登录 Token 和当前用户信息由 `features/auth` 的 Pinia store 管理，并配合集中式路由鉴权逻辑。
7. 组件级状态使用 `ref`、`reactive` 和 `computed`；跨组件或跨路由状态使用 Pinia；分页、筛选、排序等可分享状态优先使用 Vue Router query。
8. 优先使用 Element Plus 组件及其表单校验、消息提示和确认弹窗；公共组件放在 `src/components`，领域专属组件放在对应 feature。
9. 新增受保护页面时，同时配置路由懒加载、`meta` 权限信息和主布局菜单。
10. 预期业务错误在提交处提示，网络错误、401 和通用 5xx 由 Axios 拦截器统一处理；非预期异常通过应用或路由级错误兜底处理。
11. 保持 `@` 路径别名和 `/api` 请求前缀约定，不在业务代码中写死后端完整地址。
12. 单元和组件测试使用 Vitest + Vue Test Utils，API 使用 mock，关键流程补充端到端测试；测试与源码就近维护。

## 8. 安全与配置

- `application*.properties` 中的数据库密码和 JWT secret 仅适用于本地开发；生产环境应通过环境变量或外部配置注入。
- 不要提交真实数据库凭据、生产 JWT secret、用户密码或 Token。
- JWT 当前为无状态认证，退出登录主要由前端清理本地会话；如需服务端撤销，需另行设计 Token 黑名单或会话存储。
- 用户名、邮箱等标识字段是否大小写敏感，应与数据库排序规则和业务需求保持一致。

## 9. 交付前检查清单

- [ ] `mvn compile` 通过
- [ ] `mvn spring-boot:run` 可启动，Flyway 迁移成功
- [ ] `npm run build` 通过
- [ ] `npm test -- --run` 通过
- [ ] 使用 `admin / 123456` 登录成功
- [ ] 受保护接口能正确携带 JWT，失效 Token 返回 401 并回到登录页
- [ ] 新增或修改的 CRUD 功能已验证成功
- [ ] 数据库迁移可重复执行且没有修改历史版本脚本
- [ ] 未提交敏感信息、构建产物或依赖缓存

## 10. 协作流程

1. 先阅读相关模块、配置和迁移脚本，再修改代码。
2. 涉及多个文件或数据结构变更时，先说明目标、涉及文件和验证方式。
3. 小步修改，每完成一个阶段就运行对应的编译、构建或接口验证。
4. 保留与现有实现兼容的 API、字段和目录约定；如需破坏性变更，先明确说明影响范围。
5. 最终报告应说明修改文件、验证命令和仍存在的限制。
