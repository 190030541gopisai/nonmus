import { apiClient } from "./client";
import { getAccessToken } from "../features/auth/utils/tokenStorage";

apiClient.interceptors.request.use((config) => {
  const token = getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});
