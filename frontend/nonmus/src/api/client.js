import axios from "axios";
import {refreshApi} from "../features/auth/api/authApi.js";

export const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

export const apiClient = axios.create({
  baseURL: API_BASE,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true
});

apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (
          error.response?.status === 401 &&
          !originalRequest._retry
      ) {
        originalRequest._retry = true;

        await refreshApi(); // sets new HttpOnly access token cookie

        return apiClient(originalRequest); // retry original request
      }

      return Promise.reject(error);
    }
);
