import api from "./axiosConfig";

export const fetchHomeData = async () => {
  const response = await api.get("/api/home");
  return response.data;
};

const compactParams = (params = {}) =>
  Object.fromEntries(Object.entries(params).filter(([, value]) => value !== "" && value !== null && value !== undefined));

export const fetchCartProducts = async (category) => {
  const response = await api.get("/api/cart", {
    params: { category },
  });
  return response.data;
};

export const fetchProducts = async (filters = {}) => {
  const response = await api.get("/api/products", {
    params: compactParams(filters),
  });
  return response.data;
};

export const fetchProductDetails = async (productId) => {
  const response = await api.get(`/api/product/${productId}`);
  return response.data;
};

export const fetchAdminProducts = async (filters = {}) => {
  const response = await api.get("/api/admin/products", {
    params: compactParams(filters),
  });
  return response.data;
};

export const createAdminProduct = async (payload) => {
  const response = await api.post("/api/admin/products", payload);
  return response.data;
};

export const updateAdminProduct = async (id, payload) => {
  const response = await api.put(`/api/admin/products/${id}`, payload);
  return response.data;
};

export const deleteAdminProduct = async (id) => {
  await api.delete(`/api/admin/products/${id}`);
};
