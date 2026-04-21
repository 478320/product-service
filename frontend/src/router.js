import { createRouter, createWebHistory } from "vue-router";
import AdminPage from "./pages/AdminPage.vue";
import SearchPage from "./pages/SearchPage.vue";

const routes = [
  { path: "/", redirect: "/search" },
  { path: "/admin", component: AdminPage },
  { path: "/search", component: SearchPage }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
