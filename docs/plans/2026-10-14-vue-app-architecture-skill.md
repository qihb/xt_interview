# Vue 应用架构技能 · 落地计划

**目标**：将用户提供的 React 架构规范迁移为符合本项目 Vue 生态的项目级 Skill，并保留其按业务功能分层、边界治理、API、状态、错误处理、权限和测试等核心理念。
**技术栈 / 约束**：Vue 3.4、`<script setup lang="ts">`、Vite 5、TypeScript、Element Plus、Pinia、Vue Router、Axios、Vitest、Playwright；项目根目录 `agent.md` 为最高优先级。
**涉及文件**：
- 新增 `.trae/skills/vue-app-architecture/SKILL.md`
- 新增本计划文档
- 不修改 `skills-lock.json`，因为这是项目本地自定义 Skill，不是外部安装依赖
**关键决策**：
- 技能名统一使用 `vue-app-architecture`，目录名与 frontmatter 保持一致。
- 目录结构以当前项目 `frontend/web/src` 为准，同时保留 feature-first 的推荐演进方式。
**执行约定**：本文件仅为计划；确认后按指定步骤执行，每完成一个阶段勾选并更新状态。

---

## 阶段 0 · 规范迁移
- [x] 0.1 将 React 概念映射为 Vue 生态概念：React Router → Vue Router、zustand → Pinia、React ErrorBoundary → `errorCaptured` / 路由错误处理等。
- [x] 0.2 补充本项目的目录、别名、Axios、Element Plus、权限和测试约定。

## 阶段 1 · 技能落地与验证
- [x] 1.1 创建 `.trae/skills/vue-app-architecture/SKILL.md`，包含合法 frontmatter、触发描述和完整正文。
- [x] 1.2 校验技能目录名、frontmatter、正文非空，并检查内容未残留不适用的 React 专属要求。
- [x] 1.3 更新本计划状态并报告新增文件与验证结果。

---

## 当前状态
- 技能已创建并完成静态格式检查。
- 技能名称已统一为 `vue-app-architecture`。
