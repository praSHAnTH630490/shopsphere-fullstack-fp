import axios from "axios";

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("online_store_token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export function getApiError(error) {
  const data = error?.response?.data;

  if (typeof data === "string") return data;
  if (data?.message) return data.message;
  if (data?.error) return data.error;

  if (data?.errors) {
    return Object.values(data.errors).join(", ");
  }

  return error?.message || "Something went wrong.";
}

export const authApi = {
  login: (data) => api.post("/auth/login", data),
  register: (data) => api.post("/auth/register", data),
};

export const productApi = {
  list: (params = {}) => api.get("/products", { params }),
  get: (id) => api.get(`/products/${id}`),
};

export const catalogApi = {
  categories: () => api.get("/categories"),
  brands: () => api.get("/brands"),
};

export const cartApi = {
  get: () => api.get("/cart"),
  add: (data) => api.post("/cart/items", data),
  update: (id, data) => api.put(`/cart/items/${id}`, data),
  remove: (id) => api.delete(`/cart/items/${id}`),
  clear: () => api.delete("/cart"),
};

export const wishlistApi = {
  get: () => api.get("/wishlist"),
  add: (productId) => api.post(`/wishlist/items/${productId}`),
  remove: (productId) => api.delete(`/wishlist/items/${productId}`),
};

export const orderApi = {
  create: (data) => api.post("/orders", data),
  list: () => api.get("/orders"),
  get: (id) => api.get(`/orders/${id}`),
  cancel: (id) => api.post(`/orders/${id}/cancel`),
};

export const paymentApi = {
  simulate: (orderId, result) =>
    api.post("/payments/simulate", { result }, { params: { orderId } }),
};

export const reviewApi = {
  list: (productId) => api.get(`/products/${productId}/reviews`),
  create: (productId, data) =>
    api.post(`/products/${productId}/reviews`, data),
  update: (reviewId, data) => api.put(`/reviews/${reviewId}`, data),
  remove: (reviewId) => api.delete(`/reviews/${reviewId}`),
};

export const userApi = {
  me: () => api.get("/users/me"),
  update: (data) => api.put("/users/me", data),
  password: (data) => api.put("/users/me/password", data),
};

export const addressApi = {
  list: () => api.get("/users/me/addresses"),
  create: (data) => api.post("/users/me/addresses", data),
  update: (id, data) => api.put(`/users/me/addresses/${id}`, data),
  remove: (id) => api.delete(`/users/me/addresses/${id}`),
};

export const contactApi = {
  send: (data) => api.post("/contact", data),
};

export const adminApi = {
  dashboard: () => api.get("/admin/dashboard"),

  products: () => api.get("/admin/products"),
  createProduct: (data) => api.post("/admin/products", data),
  updateProduct: (id, data) => api.put(`/admin/products/${id}`, data),
  removeProduct: (id) => api.delete(`/admin/products/${id}`),

  categories: () => api.get("/admin/categories"),
  createCategory: (data) => api.post("/admin/categories", data),
  updateCategory: (id, data) => api.put(`/admin/categories/${id}`, data),
  removeCategory: (id) => api.delete(`/admin/categories/${id}`),

  brands: () => api.get("/admin/brands"),
  createBrand: (data) => api.post("/admin/brands", data),
  updateBrand: (id, data) => api.put(`/admin/brands/${id}`, data),
  removeBrand: (id) => api.delete(`/admin/brands/${id}`),

  orders: () => api.get("/admin/orders"),
  updateOrderStatus: (id, status) =>
    api.patch(`/admin/orders/${id}/status`, { status }),

  inventory: () => api.get("/admin/inventory"),
  updateStock: (id, stockQuantity) =>
    api.patch(`/admin/inventory/products/${id}`, { stockQuantity }),
};
