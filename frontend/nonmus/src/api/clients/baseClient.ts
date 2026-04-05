import axios, { type AxiosInstance } from "axios";

export const createClient = (baseURL: string): AxiosInstance => {
  const apiClient = axios.create({
    baseURL: baseURL,
    timeout: 10 * 1000, // 10 second timeout
  });

  return apiClient;
};
