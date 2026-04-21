# API 文档（核心接口）

基础说明：

- 管理端接口需请求头：`X-Role: OPERATOR` 或 `X-Role: REVIEWER`
- 响应统一格式：
  - `success`：是否成功
  - `message`：提示信息
  - `data`：业务数据

## 1. 建模接口

### 1.1 创建 SPU

- `POST /api/admin/spu`

请求体：

```json
{
  "title": "巴黎世家连衣裙 新款",
  "brandId": 1,
  "categoryId": 1,
  "description": "修身款，适合宴会场景",
  "spuAttributes": [
    { "attrName": "材质", "attrValue": "真丝" },
    { "attrName": "风格", "attrValue": "礼服" }
  ]
}
```

### 1.2 创建 SKU

- `POST /api/admin/sku`

请求体：

```json
{
  "spuId": 1,
  "skuCode": "SKU-PARIS-001",
  "skuName": "巴黎世家连衣裙 黑色 M",
  "price": 2599,
  "stock": 100,
  "sales": 0,
  "skuAttributes": [
    { "attrName": "颜色", "attrValue": "黑色" },
    { "attrName": "尺码", "attrValue": "M" }
  ]
}
```

### 1.3 更新 SPU

- `PUT /api/admin/spu/{id}`

## 2. 发布与审核接口

### 2.1 提交审核

- `POST /api/admin/publish/submit-review`

请求体：

```json
{
  "spuId": 1,
  "strategy": "IMMEDIATE",
  "scheduledPublishTime": null,
  "operator": "alice"
}
```

`strategy` 支持：

- `IMMEDIATE`
- `SCHEDULED`
- `MANUAL_AFTER_REVIEW`

### 2.2 审核通过

- `POST /api/admin/review/{taskId}/approve`

请求体：

```json
{
  "reviewer": "bob",
  "comment": "审核通过"
}
```

### 2.3 审核驳回

- `POST /api/admin/review/{taskId}/reject`

### 2.4 人工发布

- `POST /api/admin/publish/execute`

请求体：

```json
{
  "spuId": 1,
  "operator": "alice"
}
```

### 2.5 商品下架

- `POST /api/admin/publish/off-shelf`

## 3. 搜索接口

### 3.1 用户搜索商品

- `GET /api/search/products?q=&brand=&category=&pageNo=1&pageSize=10`

示例：

`GET /api/search/products?q=巴黎世家连衣裙&pageNo=1&pageSize=10`

返回示例：

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "total": 120,
    "pageNo": 1,
    "pageSize": 10,
    "records": [
      {
        "spuId": 1,
        "skuId": 1,
        "spuTitle": "巴黎世家连衣裙 001",
        "skuName": "巴黎世家连衣裙 001 标准款",
        "brandName": "巴黎世家",
        "categoryName": "连衣裙",
        "sales": 4999,
        "highlight": "<em>巴黎世家</em><em>连衣裙</em> 001"
      }
    ]
  }
}
```

### 3.2 重建索引

- `POST /api/admin/search/reindex`

## 4. 其它管理接口

- `GET /api/admin/brand`
- `GET /api/admin/category`
- `GET /api/admin/category-attribute?categoryId=1`
- `GET /api/admin/publish/tasks?limit=100`
- `GET /api/admin/logs?limit=100`

## 5. 状态枚举

`PublishStatus`：

- `DRAFT`
- `PENDING_REVIEW`
- `REVIEW_REJECTED`
- `REVIEW_PASSED`
- `WAITING_PUBLISH`
- `PUBLISHED`
- `OFF_SHELF`
