import api from "./axiosConfig";

export const registerUser = async (payload) => {
  const response = await api.post("/api/auth/register", payload);
  return response.data;
};

export const loginUser = async (payload) => {
  const response = await api.post("/api/auth/login", payload);
  return response.data;
};

export const getCustomerProfile = async () => {
  const response = await api.get("/api/customer/profile");
  return response.data;
};

export const getCustomerOrders = async () => {
  const response = await api.get("/api/customer/orders");
  return response.data;
};
