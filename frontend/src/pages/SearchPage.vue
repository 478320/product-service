<template>
  <div class="search-page">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <span>用户搜索页</span>
          <el-tag type="success">品牌优先排序已启用</el-tag>
        </div>
      </template>

      <el-row :gutter="12">
        <el-col :span="10">
          <el-input v-model="query.q" placeholder="请输入关键词，例如：巴黎世家连衣裙" @keyup.enter="search" />
        </el-col>
        <el-col :span="5">
          <el-input v-model="query.brand" placeholder="品牌筛选，例如：巴黎世家" />
        </el-col>
        <el-col :span="5">
          <el-input v-model="query.category" placeholder="类目筛选，例如：连衣裙" />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" style="width: 100%" @click="search">搜索</el-button>
        </el-col>
      </el-row>

      <el-alert
        class="mt12"
        type="info"
        :closable="false"
        title="排序策略：_score desc -> sales desc -> created_at desc，其中品牌词命中权重最高。"
      />

      <el-table :data="records" class="mt12" stripe>
        <el-table-column prop="spuId" label="SPU ID" width="90" />
        <el-table-column prop="skuId" label="SKU ID" width="90" />
        <el-table-column prop="brandName" label="品牌" width="120" />
        <el-table-column prop="categoryName" label="类目" width="120" />
        <el-table-column prop="spuTitle" label="SPU标题" min-width="220" />
        <el-table-column prop="skuName" label="SKU标题" min-width="220" />
        <el-table-column prop="sales" label="销量" width="100" />
      </el-table>

      <el-empty v-if="records.length === 0" description="暂无搜索结果" class="mt12" />
      <el-card v-for="item in records" :key="item.skuId" class="mt12" shadow="hover">
        <div>
          <b>{{ item.brandName }}</b> / {{ item.categoryName }} / {{ item.spuTitle }}
        </div>
        <div style="margin-top: 6px">SKU: {{ item.skuName }} | 销量: {{ item.sales }}</div>
        <div v-if="item.highlight" class="highlight" v-html="item.highlight"></div>
      </el-card>

      <el-pagination
        class="mt12"
        background
        layout="prev, pager, next, total"
        :total="total"
        :current-page="query.pageNo"
        :page-size="query.pageSize"
        @current-change="onPageChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { api } from "../api/service";

const total = ref(0);
const records = ref([]);
const query = reactive({
  q: "巴黎世家连衣裙",
  brand: "",
  category: "",
  pageNo: 1,
  pageSize: 10
});

async function search() {
  try {
    const res = await api.searchProducts({
      q: query.q || undefined,
      brand: query.brand || undefined,
      category: query.category || undefined,
      pageNo: query.pageNo,
      pageSize: query.pageSize
    });
    total.value = res.data.total || 0;
    records.value = res.data.records || [];
  } catch (err) {
    ElMessage.error(err.message || "搜索失败");
  }
}

function onPageChange(page) {
  query.pageNo = page;
  search();
}

search();
</script>

<style scoped>
.search-page {
  max-width: 1200px;
  margin: 0 auto;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mt12 {
  margin-top: 12px;
}

.highlight {
  margin-top: 8px;
  color: #d90429;
}
</style>
