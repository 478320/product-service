<template>
  <el-container style="min-height: 100vh">
    <el-header class="top-nav">
      <div class="logo">商品全链路实验</div>
      <div class="nav-links">
        <router-link to="/search">用户搜索页</router-link>
        <router-link to="/admin">管理端</router-link>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, #1d3557, #457b9d);
  color: #fff;
}

.logo {
  font-size: 20px;
  font-weight: 700;
}

.nav-links {
  display: flex;
  gap: 18px;
}

.nav-links a {
  color: #fff;
  text-decoration: none;
  font-weight: 600;
}

.nav-links a.router-link-active {
  border-bottom: 2px solid #f1faee;
}
</style>
