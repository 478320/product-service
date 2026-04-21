<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span>管理端</span>
          <div style="display: flex; gap: 12px; align-items: center">
            <span>当前角色</span>
            <el-select v-model="role" style="width: 140px">
              <el-option label="OPERATOR" value="OPERATOR" />
              <el-option label="REVIEWER" value="REVIEWER" />
            </el-select>
            <el-button type="primary" @click="reloadAll">刷新数据</el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="active">
        <el-tab-pane label="品牌/类目/属性" name="meta">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-card>
                <template #header>创建品牌</template>
                <el-input v-model="brandForm.name" placeholder="品牌名" />
                <el-input v-model="brandForm.description" placeholder="描述" style="margin-top: 8px" />
                <el-input-number v-model="brandForm.priority" :min="1" :max="999" style="margin-top: 8px" />
                <div class="mt8">
                  <el-button type="primary" @click="createBrand">保存品牌</el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card>
                <template #header>创建类目</template>
                <el-input v-model="categoryForm.name" placeholder="类目名称" />
                <div class="mt8">
                  <el-button type="primary" @click="createCategory">保存类目</el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card>
                <template #header>创建类目属性</template>
                <el-select v-model="attrForm.categoryId" placeholder="类目" style="width: 100%">
                  <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
                <el-input v-model="attrForm.attrName" placeholder="属性名" class="mt8" />
                <el-select v-model="attrForm.attrScope" class="mt8" style="width: 100%">
                  <el-option label="SPU" value="SPU" />
                  <el-option label="SKU" value="SKU" />
                </el-select>
                <el-select v-model="attrForm.requiredFlag" class="mt8" style="width: 100%">
                  <el-option label="必填" :value="1" />
                  <el-option label="非必填" :value="0" />
                </el-select>
                <div class="mt8">
                  <el-button type="primary" @click="createAttr">保存属性</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-divider />
          <el-row :gutter="16">
            <el-col :span="12">
              <el-table :data="brands" height="260">
                <el-table-column prop="id" label="ID" width="70" />
                <el-table-column prop="name" label="品牌" />
                <el-table-column prop="priority" label="优先级" width="90" />
              </el-table>
            </el-col>
            <el-col :span="12">
              <el-table :data="categories" height="260">
                <el-table-column prop="id" label="ID" width="70" />
                <el-table-column prop="name" label="类目" />
              </el-table>
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane label="SPU/SKU建模" name="model">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-card>
                <template #header>创建SPU</template>
                <el-input v-model="spuForm.title" placeholder="SPU标题" />
                <el-select v-model="spuForm.brandId" placeholder="品牌" class="mt8" style="width: 100%">
                  <el-option v-for="item in brands" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
                <el-select v-model="spuForm.categoryId" placeholder="类目" class="mt8" style="width: 100%">
                  <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
                <el-input v-model="spuForm.description" type="textarea" :rows="3" class="mt8" placeholder="描述" />
                <el-input v-model="spuForm.attrLine" class="mt8" placeholder="SPU属性，格式：材质=真丝;风格=礼服" />
                <div class="mt8">
                  <el-button type="primary" @click="createSpu">创建SPU</el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card>
                <template #header>创建SKU</template>
                <el-select v-model="skuForm.spuId" placeholder="选择SPU" style="width: 100%">
                  <el-option v-for="item in spus" :key="item.id" :label="item.title" :value="item.id" />
                </el-select>
                <el-input v-model="skuForm.skuCode" class="mt8" placeholder="SKU编码" />
                <el-input v-model="skuForm.skuName" class="mt8" placeholder="SKU名称" />
                <el-input-number v-model="skuForm.price" :min="1" :step="10" class="mt8" style="width: 100%" />
                <el-input-number v-model="skuForm.stock" :min="0" :step="1" class="mt8" style="width: 100%" />
                <el-input v-model="skuForm.attrLine" class="mt8" placeholder="SKU属性，格式：颜色=黑色;尺码=M" />
                <div class="mt8">
                  <el-button type="primary" @click="createSku">创建SKU</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-divider />
          <el-table :data="spus" height="280">
            <el-table-column prop="id" label="SPU ID" width="90" />
            <el-table-column prop="title" label="标题" min-width="220" />
            <el-table-column prop="publishStatus" label="状态" width="160" />
            <el-table-column prop="publishStrategy" label="策略" width="180" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="发布与审核" name="publish">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-card>
                <template #header>提交审核</template>
                <el-select v-model="publishForm.spuId" placeholder="SPU" style="width: 100%">
                  <el-option v-for="item in spus" :key="item.id" :label="item.title" :value="item.id" />
                </el-select>
                <el-select v-model="publishForm.strategy" class="mt8" style="width: 100%">
                  <el-option label="IMMEDIATE" value="IMMEDIATE" />
                  <el-option label="SCHEDULED" value="SCHEDULED" />
                  <el-option label="MANUAL_AFTER_REVIEW" value="MANUAL_AFTER_REVIEW" />
                </el-select>
                <el-date-picker
                  v-model="publishForm.scheduledPublishTime"
                  type="datetime"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="定时发布时间"
                  class="mt8"
                  style="width: 100%"
                />
                <div class="mt8">
                  <el-button type="primary" @click="submitReview">提交</el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card>
                <template #header>审核动作（需 REVIEWER）</template>
                <el-input-number v-model="reviewForm.taskId" :min="1" style="width: 100%" />
                <el-input v-model="reviewForm.comment" class="mt8" placeholder="审核备注" />
                <div class="mt8" style="display: flex; gap: 8px">
                  <el-button type="success" @click="approveTask">通过</el-button>
                  <el-button type="danger" @click="rejectTask">驳回</el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card>
                <template #header>人工发布/下架</template>
                <el-input-number v-model="manualForm.spuId" :min="1" style="width: 100%" />
                <div class="mt8" style="display: flex; gap: 8px">
                  <el-button type="primary" @click="executePublish">人工发布</el-button>
                  <el-button type="warning" @click="offShelf">下架</el-button>
                </div>
                <div class="mt8">
                  <el-button @click="reindex">重建索引</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-divider />
          <el-table :data="tasks" height="300">
            <el-table-column prop="id" label="任务ID" width="90" />
            <el-table-column prop="spuId" label="SPU" width="90" />
            <el-table-column prop="strategy" label="策略" width="170" />
            <el-table-column prop="taskStatus" label="任务状态" width="150" />
            <el-table-column prop="failReason" label="失败原因" min-width="160" />
            <el-table-column prop="createdAt" label="创建时间" width="180" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="简化日志" name="logs">
          <el-alert
            type="warning"
            show-icon
            title="日志模块为简化实现：仅关键动作落库，不包含完整审计链路。"
            :closable="false"
          />
          <el-table :data="logs" height="400" class="mt8">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="bizType" label="业务类型" width="120" />
            <el-table-column prop="bizId" label="业务ID" width="100" />
            <el-table-column prop="action" label="动作" width="180" />
            <el-table-column prop="operator" label="操作者" width="120" />
            <el-table-column prop="detail" label="详情" min-width="260" />
            <el-table-column prop="createdAt" label="时间" width="180" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { api } from "../api/service";

const role = ref("OPERATOR");
const active = ref("meta");
const brands = ref([]);
const categories = ref([]);
const spus = ref([]);
const tasks = ref([]);
const logs = ref([]);

const brandForm = reactive({ name: "", description: "", priority: 10 });
const categoryForm = reactive({ name: "" });
const attrForm = reactive({ categoryId: null, attrName: "", attrScope: "SPU", requiredFlag: 1, dataType: "TEXT" });
const spuForm = reactive({ title: "", brandId: null, categoryId: null, description: "", attrLine: "" });
const skuForm = reactive({ spuId: null, skuCode: "", skuName: "", price: 999, stock: 100, attrLine: "" });
const publishForm = reactive({ spuId: null, strategy: "IMMEDIATE", scheduledPublishTime: "" });
const reviewForm = reactive({ taskId: 1, comment: "" });
const manualForm = reactive({ spuId: 1 });

function parseAttrs(line) {
  if (!line) return [];
  return line
    .split(";")
    .map((part) => part.trim())
    .filter(Boolean)
    .map((pair) => {
      const [attrName, attrValue] = pair.split("=");
      return { attrName: (attrName || "").trim(), attrValue: (attrValue || "").trim() };
    })
    .filter((x) => x.attrName && x.attrValue);
}

async function reloadAll() {
  try {
    const [brandRes, categoryRes, spuRes, taskRes, logRes] = await Promise.all([
      api.listBrands(role.value),
      api.listCategories(role.value),
      api.listSpu(80, role.value),
      api.listTasks(120, role.value),
      api.logs(120, role.value)
    ]);
    brands.value = brandRes.data || [];
    categories.value = categoryRes.data || [];
    spus.value = spuRes.data || [];
    tasks.value = taskRes.data || [];
    logs.value = logRes.data || [];
  } catch (err) {
    ElMessage.error(err.message || "加载失败");
  }
}

async function createBrand() {
  try {
    await api.createBrand({ ...brandForm }, role.value);
    ElMessage.success("品牌已创建");
    brandForm.name = "";
    brandForm.description = "";
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function createCategory() {
  try {
    await api.createCategory({ ...categoryForm }, role.value);
    ElMessage.success("类目已创建");
    categoryForm.name = "";
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function createAttr() {
  try {
    await api.createCategoryAttr({ ...attrForm }, role.value);
    ElMessage.success("属性已创建");
    attrForm.attrName = "";
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function createSpu() {
  try {
    await api.createSpu(
      {
        title: spuForm.title,
        brandId: spuForm.brandId,
        categoryId: spuForm.categoryId,
        description: spuForm.description,
        spuAttributes: parseAttrs(spuForm.attrLine)
      },
      role.value
    );
    ElMessage.success("SPU创建成功");
    spuForm.title = "";
    spuForm.description = "";
    spuForm.attrLine = "";
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function createSku() {
  try {
    await api.createSku(
      {
        spuId: skuForm.spuId,
        skuCode: skuForm.skuCode,
        skuName: skuForm.skuName,
        price: skuForm.price,
        stock: skuForm.stock,
        sales: 0,
        skuAttributes: parseAttrs(skuForm.attrLine)
      },
      role.value
    );
    ElMessage.success("SKU创建成功");
    skuForm.skuCode = "";
    skuForm.skuName = "";
    skuForm.attrLine = "";
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function submitReview() {
  try {
    await api.submitReview(
      {
        spuId: publishForm.spuId,
        strategy: publishForm.strategy,
        scheduledPublishTime: publishForm.scheduledPublishTime || null,
        operator: role.value
      },
      role.value
    );
    ElMessage.success("提交审核成功");
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function approveTask() {
  try {
    await api.approve(
      reviewForm.taskId,
      { reviewer: "reviewer-ui", comment: reviewForm.comment || "审核通过" },
      "REVIEWER"
    );
    ElMessage.success("审核通过");
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function rejectTask() {
  try {
    await api.reject(
      reviewForm.taskId,
      { reviewer: "reviewer-ui", comment: reviewForm.comment || "审核驳回" },
      "REVIEWER"
    );
    ElMessage.success("审核驳回");
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function executePublish() {
  try {
    await api.publishExecute({ spuId: manualForm.spuId, operator: role.value }, role.value);
    ElMessage.success("人工发布成功");
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function offShelf() {
  try {
    await api.offShelf({ spuId: manualForm.spuId, operator: role.value }, role.value);
    ElMessage.success("下架成功");
    await reloadAll();
  } catch (err) {
    ElMessage.error(err.message);
  }
}

async function reindex() {
  try {
    const res = await api.reindex(role.value);
    ElMessage.success(`重建索引完成: ${res.data.reindexedCount}`);
  } catch (err) {
    ElMessage.error(err.message);
  }
}

onMounted(reloadAll);
</script>

<style scoped>
.page {
  max-width: 1400px;
  margin: 0 auto;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mt8 {
  margin-top: 8px;
}
</style>
