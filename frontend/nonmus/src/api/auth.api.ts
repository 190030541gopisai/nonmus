import { authClient } from "./clients/authClient";
import type {
  ApiResponse,
  RegisterRequest,
  RegisterResponse,
  LoginRequest,
  LoginResponse,
  VerifyOtpRequest,
  VerifyOtpResponse,
  ResendOtpRequest,
  ResendOtpResponse,
} from "../types";

export const authApi = {
  register: async (
    data: RegisterRequest,
  ): Promise<ApiResponse<RegisterResponse>> => {
    const response = await authClient.post<ApiResponse<RegisterResponse>>(
      "/register",
      data,
    );
    return response.data;
  },

  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await authClient.post<ApiResponse<LoginResponse>>(
      "/login",
      data,
    );
    return response.data;
  },

  verifyOtp: async (
    data: VerifyOtpRequest,
  ): Promise<ApiResponse<VerifyOtpResponse>> => {
    const response = await authClient.post<ApiResponse<VerifyOtpResponse>>(
      "/verify-otp",
      data,
    );
    return response.data;
  },

  resendOtp: async (
    data: ResendOtpRequest,
  ): Promise<ApiResponse<ResendOtpResponse>> => {
    const response = await authClient.post<ApiResponse<ResendOtpResponse>>(
      "/resend-otp",
      data,
    );
    return response.data;
  },
};
