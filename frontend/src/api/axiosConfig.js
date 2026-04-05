import axios from "axios";

const resolvedBaseUrl = import.meta.env.DEV ? "" : (import.meta.env.VITE_API_BASE_URL || "");

const api = axios.create({
  baseURL: resolvedBaseUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

const RETRYABLE_METHODS = new Set(["get", "head", "options"]);
const RETRYABLE_STATUS = new Set([502, 503, 504]);
const RETRYABLE_AUTH_PATHS = new Set(["/api/auth/login", "/api/auth/register"]);
const PUBLIC_PATH_PREFIXES = ["/api/home", "/api/products", "/api/cart", "/api/product", "/api/auth"];
const MAX_RETRIES = 10;
const RETRY_DELAY_MS = 1500;

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const isPublicPath = (path) => PUBLIC_PATH_PREFIXES.some((prefix) => path.startsWith(prefix));

api.interceptors.request.use((config) => {
  const requestPath = config.url || "";
  const token = localStorage.getItem("token");
  if (token && !isPublicPath(requestPath)) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const config = error.config;
    if (!config) {
      return Promise.reject(error);
    }

    const method = (config.method || "get").toLowerCase();
    const status = error.response?.status;
    const networkError = !error.response;
    const requestPath = config.url || "";
    const isAuthPost = method === "post" && RETRYABLE_AUTH_PATHS.has(requestPath);
    const shouldRetry = (RETRYABLE_METHODS.has(method) || isAuthPost) && (networkError || RETRYABLE_STATUS.has(status));

    if (!shouldRetry) {
      return Promise.reject(error);
    }

    config.__retryCount = config.__retryCount || 0;
    if (config.__retryCount >= MAX_RETRIES) {
      return Promise.reject(error);
    }

    config.__retryCount += 1;
    await sleep(RETRY_DELAY_MS);
    return api(config);
  }
);

export default api;
