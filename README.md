# 商品全链路实验项目（Java + MySQL + Elasticsearch）

本项目实现了从商品建模到发布再到搜索的全链路：

- 商品建模：`SPU + SKU + 类目 + 属性模板（SPU/SKU分层）`
- 商品发布：发布状态机、发布策略区分、违禁词检测、人工审核
- 商品搜索：MySQL 数据发布后写入 ES 倒排索引，用户检索按品牌高权重排序

## 1. 技术栈

- 后端：Spring Boot 3.3.5 + JDK17 + Maven + MyBatis
- 数据库：MySQL 8.0
- 搜索：Elasticsearch 8.13.4（IK 分词器镜像）
- 前端：Vue3 + Vite + Element Plus（管理端 + 用户搜索页）

## 2. 目录结构

- `backend`：后端服务
- `frontend`：前端服务
- `backend/src/main/resources/sql/schema.sql`：建表脚本
- `backend/src/main/resources/sql/seed_data.sql`：500条样例数据脚本
- `docs/API.md`：核心接口说明
- `docs/EXPERIMENT.md`：实验步骤手册

## 3. 一键启动依赖（MySQL + ES）

在项目根目录执行：

```bash
docker compose up -d
```

说明：
- MySQL 默认端口 `3306`，账号 `root`，密码 `123456`
- ES 默认端口 `9200`
- 初始化 SQL 会自动从 `backend/src/main/resources/sql` 导入

## 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

Swagger UI：`http://localhost:8080/swagger-ui.html`

## 5. 启动前端

PowerShell 可能被本机执行策略拦截 `npm`，可使用 `cmd` 启动：

```bash
cd frontend
cmd /c npm install
cmd /c npm run dev
```

前端地址：`http://localhost:5173`

## 6. 关键业务说明

### 6.1 商品建模（重点精细实现）

- SPU 表达抽象商品，SKU 表达可售单元
- 类目属性支持 SPU/SKU 分层、必填约束
- 发布前会校验必填属性：
  - SPU 必填属性缺失 -> 禁止提交审核
  - SKU 必填属性缺失 -> 禁止提交审核

### 6.2 商品发布（重点精细实现）

状态机：

`DRAFT -> PENDING_REVIEW -> REVIEW_REJECTED | REVIEW_PASSED -> WAITING_PUBLISH -> PUBLISHED -> OFF_SHELF`

发布策略：

- `IMMEDIATE`：审核通过即发布
- `SCHEDULED`：到达定时点自动发布
- `MANUAL_AFTER_REVIEW`：审核通过后人工触发发布

审核和风控：

- 提交审核和执行发布时都会做违禁词检测（大小写归一）
- 人工审核为单级审核（通过/驳回）

### 6.3 日志模块（按要求“简化实现”）

已按你的要求**一笔带过但明确声明**：

- 仅将关键动作记录到 `operation_log`
- 不实现复杂审计平台、链路追踪、指标告警

### 6.4 商品搜索（重点精细实现）

- ES 索引：`product_search_v1`（SKU 文档模型）
- 中文分词：`ik_max_word`（索引） + `ik_smart`（查询）
- 查询策略：`multi_match + function_score`
- 权重设计：品牌 > 商品名 > 类目 > 属性
- 过滤条件：仅 `PUBLISHED` 商品可被搜索
- 排序：`_score desc -> sales desc -> createdAt desc`

验证示例：

搜索 `巴黎世家连衣裙` 时，优先返回巴黎世家相关商品。

## 7. 样例数据说明（500条）

`seed_data.sql` 会初始化：

- 8 个品牌
- 6 个类目
- 类目属性模板（SPU/SKU 必填）
- 500 个 SPU + 500 个 SKU
- 发布任务、审核记录、操作日志
- 重点构造“巴黎世家 + 连衣裙”高相关样本

## 8. 核心接口快速验证

1) 搜索接口（用户侧）：

```bash
curl "http://localhost:8080/api/search/products?q=巴黎世家连衣裙&pageNo=1&pageSize=10"
```

2) 重建索引（管理侧）：

```bash
curl -X POST "http://localhost:8080/api/admin/search/reindex" -H "X-Role: OPERATOR"
```

3) 查看发布任务：

```bash
curl "http://localhost:8080/api/admin/publish/tasks?limit=20" -H "X-Role: OPERATOR"
```

## 9. 测试

```bash
cd backend
mvn test
```

覆盖了建模必填属性校验与发布提审关键分支。
