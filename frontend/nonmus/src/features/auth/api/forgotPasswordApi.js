import { apiClient } from "../../../api/client";

const PREFIX = "/v1/forgot-password";

export const sendForgotPasswordOtp = async (email) => {
  const response = await apiClient.post(`${PREFIX}/send`, { email });
  return response.data;
};

export const verifyForgotPasswordOtp = async (email, code) => {
  const response = await apiClient.post(`${PREFIX}/verify`, { email, code });
  return response.data;
};

export const resetForgotPassword = async (newPassword) => {
  const response = await apiClient.post(`${PREFIX}/reset`, { newPassword });
  return response.data;
};
