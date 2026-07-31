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
  await apiClient.post(`${PREFIX}/refresh`);
};

export const logoutApi = async () => {
  await apiClient.post(`${PREFIX}/logout`)
}

export const resendVerificationEmailApi = async () => {
  const response = await apiClient.post("/v1/email/resend");
  return response.data;
};

export const verifyEmailApi = async (token) => {
  const response = await apiClient.get(`/v1/email/verify?token=${token}`);
  return response.data;
};