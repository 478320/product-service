# 实验手册（一步步跑通全链路）

## 1. 启动依赖

```bash
docker compose up -d
```

如果端口冲突，请修改 `docker-compose.yml` 的端口映射。

## 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

## 3. 启动前端

```bash
cd frontend
cmd /c npm install
cmd /c npm run dev
```

访问：

- 管理端：`http://localhost:5173/admin`
- 搜索页：`http://localhost:5173/search`

## 4. 验证商品建模

1. 在管理端创建品牌、类目、类目属性（SPU/SKU 必填）
2. 创建 SPU 并填入 SPU 属性
3. 创建 SKU 并填入 SKU 属性
4. 提交审核时，若缺少必填属性会被阻断

## 5. 验证发布引擎

1. 提交审核（选择 `IMMEDIATE` / `SCHEDULED` / `MANUAL_AFTER_REVIEW`）
2. 用 `REVIEWER` 角色执行审核通过或驳回
3. 对于 `MANUAL_AFTER_REVIEW`，在管理端手工触发发布
4. 已发布商品可执行下架

## 6. 验证搜索与品牌优先

1. 调用重建索引：`POST /api/admin/search/reindex`
2. 在搜索页输入：`巴黎世家连衣裙`
3. 预期：巴黎世家相关商品优先展示

也可直接调用：

```bash
curl "http://localhost:8080/api/search/products?q=巴黎世家连衣裙&pageNo=1&pageSize=10"
```

## 7. 查看简化日志

管理端日志页或接口：

`GET /api/admin/logs?limit=100`

说明：日志模块是简化实现，只记录关键动作，不包含复杂可观测平台能力。
