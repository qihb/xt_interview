# 测试说明

## 前端

前端使用 Vitest、Vue Test Utils 和 jsdom，测试文件与源码同目录，统一使用 `*.test.ts` 命名。

```bash
cd frontend/web
npm run test
npm run test:coverage
npm run build
```

测试边界如下：

- API 与请求拦截器使用 `vi.mock` 隔离 Axios、路由和 Element Plus 消息。
- Pinia 测试使用独立的 `createPinia()`，每个用例清理本地存储。
- 页面测试只验证挂载、列表加载和关键渲染行为，Element Plus 组件使用轻量 Stub。
- 首轮覆盖率不设置强制阈值，当前语句覆盖率基线约为 78.46%。

## 后端

后端使用 JUnit 5、Mockito、独立 MockMvc 和 JaCoCo，不依赖本地 MySQL 或运行中的后端服务。

```bash
mvn test
mvn verify
```

Service 测试使用 Mockito 隔离 Repository；Controller 测试使用 MockMvc 验证 HTTP 方法、路径、请求体和响应 JSON；`JwtUtil` 与 `MD5Util` 使用纯单元测试覆盖认证基础逻辑。

项目要求使用 Java 26。若 Maven 报 `不支持发行版本 26`，请先切换 Maven 使用的 JDK，再执行上述命令。首轮暂不引入 `@SpringBootTest`、Testcontainers 或真实数据库集成测试。
