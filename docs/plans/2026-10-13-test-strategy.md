# 前后端单元测试 · 技术选型与落地计划

**目标**：为 Vue 前端和 Spring Boot 后端建立可重复执行的测试基础设施，并优先覆盖登录、用户管理、部门管理及输入校验等核心业务。
**技术栈 / 约束**：前端 Vue 3 + TypeScript + Vite 5；后端 Spring Boot 4.0.6 + Java 26 + JPA；数据库 MySQL 8.0。首轮以单元测试和 Web 层切片测试为主，不依赖本地数据库或正在运行的后端服务。
**涉及文件**：`frontend/web/package.json`、`frontend/web/vite.config.ts`、`frontend/web/src/**` 对应测试文件、`src/test/java/**`、`pom.xml`（仅在需要补充测试插件或报告配置时修改）、项目测试说明文档。
**关键决策**：
- 前端选用 Vitest + `@vue/test-utils` + `jsdom`，与 Vite 原生集成，使用 `vi.mock` 隔离 Axios/API 和 Pinia 依赖。
- 后端选用 JUnit 5 + Mockito + Spring `MockMvc`；Controller 使用 `@WebMvcTest`，Service 使用 Mockito 纯单元测试。
- 首轮不引入 Testcontainers、真实 MySQL 和 Playwright，避免测试依赖外部服务；后续再补充少量 `@SpringBootTest` 集成测试。
- 使用 JaCoCo（后端）和 Vitest coverage（前端）输出覆盖率，并将测试命令纳入项目文档/CI 入口。
**执行约定**：本文件仅为计划；由用户确认后按指定阶段执行，每完成一个阶段勾选并记录验证结果。

---

## 阶段 0 · 测试基础设施
- [x] 0.1 补充前端 Vitest、Vue Test Utils、jsdom 及 coverage 依赖，增加 `test`、`test:watch`、`test:coverage` 脚本。
- [x] 0.2 增加 Vitest 配置，复用 Vite 的 `@` 路径别名，并准备 DOM、Element Plus 和 Pinia 的测试初始化。
- [x] 0.3 确认后端 `spring-boot-starter-test` 已覆盖 JUnit 5、Mockito、MockMvc；补充 Surefire/JaCoCo 配置（若当前 Maven 默认配置不足）。
- [x] 0.4 统一测试命名、夹具（fixture）、Mock 数据和测试运行说明。

## 阶段 1 · 前端单测
- [x] 1.1 测试 `src/api/index.ts` 与 `src/utils/request.ts`：请求方法、路径、参数、错误响应和鉴权头行为。
- [x] 1.2 测试 `src/stores/user.ts`：登录成功/失败、用户状态持久化、退出登录和 token 清理。
- [x] 1.3 测试 `Login.vue`：登录页字段渲染及路由依赖隔离。
- [x] 1.4 测试 `UserManagement.vue` 与 `DepartmentManagement.vue`：列表加载和页面挂载行为。
- [x] 1.5 测试路由守卫及关键布局行为，保证未登录访问管理页时的跳转逻辑。

## 阶段 2 · 后端 Service 与 Controller 单测
- [x] 2.1 测试 `AccountServiceImpl`：创建、默认密码、重复用户名、更新、软删除和禁用账号。
- [x] 2.2 测试 `DepartmentServiceImpl`：名称/编码必填、编码重复、状态默认值和软删除。
- [x] 2.3 使用独立 `MockMvc` 测试账号和部门接口：HTTP 方法、路径、请求体和响应 JSON。
- [x] 2.4 测试 `AuthController`：用户名/密码必填、登录成功和 token 缺失校验。
- [x] 2.5 补充 `JwtUtil`、`MD5Util` 纯工具类测试。

## 阶段 3 · 集成验证与质量门禁
- [x] 3.1 前端单测和构建已通过；后端单测已完成但受当前 JDK 21 无法编译 Java 26 阻塞。
- [x] 3.2 首轮暂不引入 `@SpringBootTest`、Testcontainers 和真实数据库。
- [x] 3.3 建立前端首轮覆盖率基线：语句覆盖率约 78.46%，暂不设置强制阈值。
- [x] 3.4 新增测试说明文档，记录本地命令、测试分层、Mock 边界和后续扩展方式。

---

## 推荐命令

```bash
# 前端
npm run test
npm run test:coverage

# 后端
mvn test
mvn verify
```

## 当前状态

- 已完成测试基础设施、前端单测、后端单测代码、覆盖率配置和测试说明文档。
- 后端 `mvn test` 尚未完成运行验证，原因是当前 Maven 使用 Java 21，而项目 `pom.xml` 要求 Java 26（错误：`不支持发行版本 26`）。
