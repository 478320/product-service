import http, { withRole } from "./http";

export const api = {
  listBrands(role = "OPERATOR") {
    return http.get("/api/admin/brand", withRole(role));
  },
  createBrand(data, role = "OPERATOR") {
    return http.post("/api/admin/brand", data, withRole(role));
  },
  listCategories(role = "OPERATOR") {
    return http.get("/api/admin/category", withRole(role));
  },
  createCategory(data, role = "OPERATOR") {
    return http.post("/api/admin/category", data, withRole(role));
  },
  listCategoryAttrs(categoryId, role = "OPERATOR") {
    return http.get("/api/admin/category-attribute", {
      ...withRole(role),
      params: { categoryId }
    });
  },
  createCategoryAttr(data, role = "OPERATOR") {
    return http.post("/api/admin/category-attribute", data, withRole(role));
  },
  createSpu(data, role = "OPERATOR") {
    return http.post("/api/admin/spu", data, withRole(role));
  },
  updateSpu(id, data, role = "OPERATOR") {
    return http.put(`/api/admin/spu/${id}`, data, withRole(role));
  },
  listSpu(limit = 50, role = "OPERATOR") {
    return http.get("/api/admin/spu", {
      ...withRole(role),
      params: { limit }
    });
  },
  createSku(data, role = "OPERATOR") {
    return http.post("/api/admin/sku", data, withRole(role));
  },
  listSku(spuId, role = "OPERATOR") {
    return http.get("/api/admin/sku", {
      ...withRole(role),
      params: { spuId }
    });
  },
  submitReview(data, role = "OPERATOR") {
    return http.post("/api/admin/publish/submit-review", data, withRole(role));
  },
  publishExecute(data, role = "OPERATOR") {
    return http.post("/api/admin/publish/execute", data, withRole(role));
  },
  offShelf(data, role = "OPERATOR") {
    return http.post("/api/admin/publish/off-shelf", data, withRole(role));
  },
  listTasks(limit = 100, role = "OPERATOR") {
    return http.get("/api/admin/publish/tasks", {
      ...withRole(role),
      params: { limit }
    });
  },
  approve(taskId, data, role = "REVIEWER") {
    return http.post(`/api/admin/review/${taskId}/approve`, data, withRole(role));
  },
  reject(taskId, data, role = "REVIEWER") {
    return http.post(`/api/admin/review/${taskId}/reject`, data, withRole(role));
  },
  logs(limit = 100, role = "OPERATOR") {
    return http.get("/api/admin/logs", {
      ...withRole(role),
      params: { limit }
    });
  },
  reindex(role = "OPERATOR") {
    return http.post("/api/admin/search/reindex", {}, withRole(role));
  },
  searchProducts(params) {
    return http.get("/api/search/products", { params });
  }
};
