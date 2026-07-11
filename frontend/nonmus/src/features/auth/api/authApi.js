import { apiClient } from "../../../api/client";

const PREFIX = "/v1/auth";

export const loginApi = async (data) => {
  const response = await apiClient.post(`${PREFIX}/login`, data);
  return response.data;
};

export const signupApi = async (data) => {
  const response = await apiClient.post(`${PREFIX}/signup`, data);
  return response.data;
};

export const refreshApi = async () => {
  await apiClient.get(`${PREFIX}/refresh`);
};
