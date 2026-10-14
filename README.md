# XT Interview

XT Interview 是一个前后端分离的用户管理后台项目，已完成登录认证、用户账号管理、部门管理、用户-部门关联、分页筛选和基于角色的权限控制。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Spring Boot 4.0.6、Java 26、Spring Data JPA/Hibernate、Flyway、JWT |
| 前端 | Vue 3.4、Vite 5、TypeScript、Element Plus、Pinia、Vue Router、Axios |
| 数据库 | MySQL 8.0，`utf8mb4` / `utf8mb4_unicode_ci` |
| 构建工具 | Maven 3.6+、npm |

## 环境要求

- JDK 26
- Maven 3.6+
- Node.js 和 npm
- MySQL 8.0

## 快速启动

### 1. 创建数据库

Flyway 负责创建表和执行迁移，但不会自动创建数据库本身。启动后端前，请先在 MySQL 中执行：

```sql
CREATE DATABASE xt_interview
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

默认数据库连接配置如下：

| 配置项 | 默认值 |
| --- | --- |
| 地址 | `localhost:3306` |
| 数据库 | `xt_interview` |
| 用户名 | `root` |
| 密码 | `root` |

如需修改配置，请编辑 `src/main/resources/application-dev.properties` 或通过外部配置覆盖。不要将生产环境凭据提交到仓库。

### 2. 启动后端

在项目根目录执行：

```bash
mvn compile
mvn spring-boot:run
```

后端默认地址为 `http://localhost:8080`。启动时会自动执行 `src/main/resources/db/migration/` 下的 Flyway 脚本。

### 3. 启动前端

```bash
cd frontend/web
npm install
npm run dev
```

前端默认地址为 `http://localhost:5173`。Vite 会将 `/api` 请求代理到 `http://localhost:8080`。

生产构建：

```bash
npm run build
```

### 4. 登录

```text
用户名：admin
密码：123456
```

## 项目结构

```text
.
├── src/main/java/com/xt/interview
│   ├── controller/       # REST 控制器
│   ├── service/          # 业务接口与实现
│   ├── repository/       # Spring Data JPA 数据访问
│   ├── entity/           # 数据库实体
│   ├── dto/              # 接口数据传输对象
│   ├── filter/           # JWT 请求过滤器
│   ├── config/            # Web 和应用配置
│   └── util/              # JWT、密码等工具类
├── src/main/resources
│   ├── application*.properties
│   └── db/migration/      # Flyway 迁移脚本
├── frontend/web
│   ├── src/features/      # 按业务领域组织 API、类型、组件、composables 和 store
│   │   ├── auth/          # 登录认证、会话和权限
│   │   ├── account/       # 用户账号管理
│   │   └── department/    # 部门管理
│   ├── src/api/           # 兼容迁移中的 API 测试或全局 API
│   ├── src/components/    # 跨 feature 复用组件
│   ├── src/layouts/       # 登录后的页面布局
│   ├── src/router/        # Vue Router 配置
│   ├── src/stores/        # Pinia 状态
│   ├── src/views/         # 按业务域组织的页面视图
│   │   ├── auth/
│   │   ├── account/
│   │   └── department/
│   └── src/utils/         # 请求工具
├── agent.md               # AI 协作与开发规范
├── FUNCTION_ARCHITECTURE.md # 项目初始功能架构与问题基线
├── INTERVIEW_TASKS.md     # 面试开发任务说明
├── skills-lock.json       # 项目锁定的 skills 来源和版本信息
└── pom.xml
```

## API 概览

### 认证接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/auth/login` | 使用用户名和密码登录，返回 JWT 和用户信息 |
| `POST` | `/api/auth/logout` | 退出登录 |
| `GET` | `/api/auth/validate` | 校验 `Authorization: Bearer <token>` |

### 用户账号接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/accounts` | 查询全部账号 |
| `GET` | `/api/accounts/{id}` | 查询指定账号 |
| `POST` | `/api/accounts` | 创建账号 |
| `PUT` | `/api/accounts/{id}` | 更新账号 |
| `DELETE` | `/api/accounts/{id}` | 删除账号 |

### 部门接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/departments` | 查询全部未删除部门，用户侧由前端过滤禁用部门 |
| `GET` | `/api/departments/{id}` | 查询指定部门 |
| `POST` | `/api/departments` | 创建部门 |
| `PUT` | `/api/departments/{id}` | 更新部门；禁用有关联用户的部门时，首次请求返回 409，确认后在请求体增加 `"unassignUsers": true` 批量解除用户部门关联 |
| `DELETE` | `/api/departments/{id}` | 删除未被用户关联的部门 |

禁用部门的确认响应会在 `details.accountCount` 返回关联用户数，并通过 `details.requiresConfirmation=true` 标识需要确认。确认后的解绑和禁用在同一事务中完成，用户的 `department_id` 会被置为 `NULL`。

受保护请求使用以下请求头：

```http
Authorization: Bearer <JWT>
```

## Skills 说明

项目通过 `skills-lock.json` 锁定了以下 14 个 skills。它们用于统一开发规范、提升代码质量，并辅助前端、后端和测试工作流。

### 全局工程规范

| Skill | 用途 |
| --- | --- |
| `tdd` | 适用于整个项目，按测试驱动开发流程推进功能实现，强调先写测试、再实现、最后重构 |

### 后端与工程规范

| Skill | 用途 |
| --- | --- |
| `dr-jskill` | 创建和组织 Java / Spring Boot 应用，适用于后端模块、REST API 和数据库集成 |
| `java-springboot-standards-skill` | 约束 Spring Boot 分层、DTO/VO、校验、事务、分页、安全和数据库访问实践 |

### Vue 与前端工程

| Skill | 用途 |
| --- | --- |
| `antfu` | 提供 JavaScript / TypeScript 工程化配置和代码风格建议 |
| `vitest` | 提供 Vitest 测试编写、Mock、过滤和覆盖率配置建议 |
| `vue` | 指导 Vue 3 Composition API、`script setup`、组件通信和响应式状态设计 |
| `pinia` | 指导 Pinia store 的状态、getter、action 和类型安全实现 |
| `vite` | 指导 Vite 配置、插件、开发服务器、构建和 SSR 相关实践 |
| `typescript-advanced-types` | 处理泛型、条件类型、映射类型和模板字面量等复杂 TypeScript 类型问题 |
| `unocss` | 配置和使用 UnoCSS 原子化 CSS、快捷方式及相关预设 |

### UI、可访问性与设计规范

| Skill | 用途 |
| --- | --- |
| `shawn-design-spec` | 遵循基于 Element Plus 的 B2B 管理后台设计和代码规范 |
| `baseline-ui` | 快速优化页面的间距、层级、排版和整体视觉一致性 |
| `fixing-accessibility` | 检查和修复表单、交互控件、键盘操作、焦点管理和 ARIA 等可访问性问题 |
| `web-design-guidelines` | 按 Web 界面通用规范审查布局、交互、可读性和用户体验 |

这些 skills 是开发辅助规范，不会替代项目自身的 `agent.md`、接口约定或安全要求。实际开发时，应优先遵循项目已有实现和配置。

## 关键问题与解决方案

### 1. 统一处理软删除唯一性和接口错误

**问题**：账号软删除后仍占用用户名唯一索引，参数校验、业务冲突和权限错误也容易直接表现为默认 500，前端无法定位具体字段。

**解决方案**：补充用户名和部门编码的业务层重复校验，增加全局异常处理器，统一返回 `status`、`message`、`details`，并将字段级错误传递给前端。

### 2. 修复 MySQL 原生查询返回类型不匹配

**问题**：部门编码重复校验使用原生 `SELECT EXISTS(...)` 时，MySQL 返回 `Long`，而 Repository 声明为 `boolean`，导致 `ClassCastException`。

**解决方案**：将查询改为 `COUNT(*)`，Repository 返回 `long`，Service 通过 `> 0` 判断是否存在，并同步修正服务层测试。

### 3. 前端业务边界和目录结构不清晰

**问题**：API、状态、页面和组件职责分散，跨模块深层导入容易造成依赖耦合，页面目录也不利于按业务维护。

**解决方案**：按 `auth`、`account`、`department` 拆分 `features`，每个 feature 通过 `index.ts` 暴露公共 API；`views` 按业务域组织，公共组件统一放在 `src/components/`。

### 4. 完善权限控制和部门禁用一致性

**问题**：仅依赖前端隐藏按钮无法阻止越权请求；部门被用户引用时直接禁用会破坏关联数据一致性。

**解决方案**：前端通过 Pinia 和统一权限工具控制按钮及事件，后端 Controller 使用 `RolePermission` 强制校验并统一返回 `403`；禁用有关联用户的部门时先返回 `409` 和关联数量，确认后以 `unassignUsers: true` 在同一事务中解绑并禁用。
