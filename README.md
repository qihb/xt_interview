# XT Interview

XT Interview 是一个前后端分离的用户管理后台项目，当前提供 JWT 登录认证和用户账号 CRUD 能力，并为部门管理、用户-部门关联以及用户搜索分页预留了扩展任务。

## 功能概览

- 用户登录、Token 校验和退出登录
- 用户账号新增、查询、编辑、软删除
- 基于 Vue Router 的登录页和后台布局
- 基于 Pinia 的用户会话状态管理
- 基于 Flyway 的数据库版本迁移
- Element Plus 管理后台界面

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

项目后端明确使用 Java 26，请先确认：

```bash
java -version
mvn -version
node --version
npm --version
```

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
│   ├── src/api/           # Axios API 封装
│   ├── src/layouts/       # 登录后的页面布局
│   ├── src/router/        # Vue Router 配置
│   ├── src/stores/        # Pinia 状态
│   ├── src/views/         # 页面视图
│   └── src/utils/         # 请求工具
├── agent.md               # AI 协作与开发规范
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

受保护请求使用以下请求头：

```http
Authorization: Bearer <JWT>
```

## 数据库迁移

- 迁移脚本位于 `src/main/resources/db/migration/`
- 文件名遵循 `V{版本}__{说明}.sql`
- 不要修改已经执行过的历史迁移脚本
- 数据库表结构以 Flyway 为准，JPA 自动建表已关闭
- 当前初始化脚本会创建账号表并写入默认管理员账号

## Skills 说明

项目通过 `skills-lock.json` 锁定了以下 14 个 skills。它们用于统一开发规范、提升代码质量，并辅助前端、后端和测试工作流。

### 后端与工程规范

| Skill | 用途 |
| --- | --- |
| `dr-jskill` | 创建和组织 Java / Spring Boot 应用，适用于后端模块、REST API 和数据库集成 |
| `java-springboot-standards-skill` | 约束 Spring Boot 分层、DTO/VO、校验、事务、分页、安全和数据库访问实践 |
| `antfu` | 提供 JavaScript / TypeScript 工程化配置和代码风格建议 |
| `tdd` | 按测试驱动开发流程推进功能实现，强调先写测试、再实现、最后重构 |
| `vitest` | 提供 Vitest 测试编写、Mock、过滤和覆盖率配置建议 |

### Vue 与前端工程

| Skill | 用途 |
| --- | --- |
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

## 开发约定

### 后端

1. 按 `entity -> repository -> service -> service/impl -> controller -> dto` 组织业务模块。
2. 接口输入使用 DTO 和校验注解，避免直接使用实体作为写入参数。
3. 删除操作优先沿用软删除模式，查询默认排除已删除数据。
4. 密码、JWT 和数据库敏感信息不得写入日志或响应。
5. 数据结构变更必须新增 Flyway 迁移脚本。

### 前端

1. 使用 Vue 3 Composition API 和 TypeScript。
2. 请求通过 `src/api` 或 `src/utils/request.ts` 统一封装。
3. 登录 Token 和当前用户信息由 Pinia 管理。
4. 优先使用 Element Plus 的表单校验、消息提示和确认弹窗。
5. 新增受保护页面时同步配置路由鉴权、布局菜单和 API 封装。
6. 保持 `@` 路径别名和 `/api` 请求前缀约定。

## 验证清单

- [ ] `mvn compile` 通过
- [ ] `mvn spring-boot:run` 可启动且 Flyway 迁移成功
- [ ] `npm run build` 通过
- [ ] 使用 `admin / 123456` 登录成功
- [ ] 受保护接口能够正确携带 JWT
- [ ] 失效 Token 能够回到登录页
- [ ] 未提交数据库凭据、JWT secret、Token 或构建产物

## 进一步开发

详细的面试开发任务和验收要求请阅读 [`INTERVIEW_TASKS.md`](./INTERVIEW_TASKS.md)，包括：

- 部门管理模块
- 用户与部门关联
- 用户列表搜索和分页

协作流程、目录约定和安全要求请阅读 [`agent.md`](./agent.md)。
