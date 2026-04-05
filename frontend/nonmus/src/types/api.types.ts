// API Response wrapper
export interface ApiResponse<T> {
  success: boolean;
  statusCode: number;
  message: string;
  data?: T;
  meta?: {
    timeStamp: string;
    requestId?: string;
  };
  errors?: {
    code: string;
    message: string;
    details?: Array<{
      field: string;
      message: string;
    }>;
  };
}

// Auth types
export interface RegisterRequest {
  firstName: string;
  lastName?: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  userId: string;
  email: string;
  firstName: string;
  lastName?: string;
  emailVerified: boolean;
  otpSent: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenInfo {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface UserInfo {
  userId: string;
  email: string;
  firstName: string;
  lastName?: string;
  emailVerified: boolean;
}

export interface LoginResponse {
  user: UserInfo;
  tokenInfo: TokenInfo;
}

export interface VerifyOtpRequest {
  userId: string;
  email: string;
  otp: string;
}

export interface VerifyOtpResponse extends TokenInfo {}

export interface ResendOtpRequest {
  userId: string;
  email: string;
}

export interface ResendOtpResponse {
  userId: string;
  retryAfter: number;
}
