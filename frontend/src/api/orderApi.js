import api from "./axiosConfig";

const compactParams = (params = {}) =>
  Object.fromEntries(Object.entries(params).filter(([, value]) => value !== "" && value !== null && value !== undefined));

export const validateCart = async (items) => {
  const response = await api.post("/api/cart/validate", items);
  return response.data;
};

export const patchStock = async (items) => {
  await api.patch("/api/products/stock", { items });
};

export const createOrder = async (payload) => {
  const response = await api.post("/api/orders", payload);
  return response.data;
};

export const fetchAdminOrders = async (filters = {}) => {
  const response = await api.get("/api/admin/orders", {
    params: compactParams(filters),
  });
  return response.data;
};

export const updateAdminOrder = async (id, soldQuantity) => {
  const response = await api.put(`/api/admin/orders/${id}`, { soldQuantity });
  return response.data;
};

export const deleteAdminOrder = async (id) => {
  await api.delete(`/api/admin/orders/${id}`);
};
