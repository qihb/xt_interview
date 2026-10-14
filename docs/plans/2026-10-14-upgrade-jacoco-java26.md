# JaCoCo 升级 · 落地计划

**目标**：将 JaCoCo 升级到正式支持 Java 26 的稳定版本。
**技术栈 / 约束**：Maven；项目 `java.version` 为 26。
**涉及文件**：`pom.xml`；本计划文档。
**关键决策**：使用 JaCoCo `0.8.15`，该版本正式支持 Java 26。
**执行约定**：本文件仅为计划；由用户指定步骤后再执行，每完成一步勾选并记录结果。

---

## 阶段 0 · 配置升级
- [x] 0.1 将 `jacoco-maven-plugin` 从 `0.8.12` 升级到 `0.8.15`
- [x] 0.2 检查 Maven 配置解析并执行覆盖率相关校验

---

## 当前状态
- 已完成 JaCoCo 版本升级。
- `mvn validate` 和 `mvn verify -DskipTests` 均执行成功，JaCoCo 报告已生成。
