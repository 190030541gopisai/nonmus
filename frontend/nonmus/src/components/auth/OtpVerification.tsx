import { useState, useEffect, type FormEvent } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { authApi } from "../../api/auth.api";
import { useAuth } from "../../context";
import { AxiosError } from "axios";
import type { ApiResponse } from "../../types";

export function OtpVerification() {
  const navigate = useNavigate();
  const location = useLocation();
  const { pendingVerification, setPendingVerification, login } = useAuth();
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  useEffect(() => {
    // If no pending verification, try to get from location state or redirect to login
    if (!pendingVerification) {
      const locationState = location.state as {
        userId?: string;
        email?: string;
      } | null;
      if (locationState?.userId && locationState?.email) {
        setPendingVerification({
          userId: locationState.userId,
          email: locationState.email,
        });
      } else {
        navigate("/login");
      }
    }
  }, [pendingVerification, location.state, navigate, setPendingVerification]);

  useEffect(() => {
    if (resendCooldown > 0) {
      const timer = setTimeout(
        () => setResendCooldown(resendCooldown - 1),
        1000,
      );
      return () => clearTimeout(timer);
    }
  }, [resendCooldown]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!pendingVerification) return;

    setError("");
    setSuccess("");
    setIsLoading(true);

    try {
      const response = await authApi.verifyOtp({
        userId: pendingVerification.userId,
        email: pendingVerification.email,
        otp,
      });

      if (response.success && response.data) {
        login(
          {
            userId: pendingVerification.userId,
            email: pendingVerification.email,
            firstName: "",
            emailVerified: true,
          },
          response.data,
        );
        setPendingVerification(null);
        navigate("/dashboard");
      }
    } catch (err) {
      if (err instanceof AxiosError && err.response?.data) {
        const apiError = err.response.data as ApiResponse<{
          attemptsRemaining?: number;
        }>;
        if (apiError.data?.attemptsRemaining !== undefined) {
          setError(
            `${apiError.message}. Attempts remaining: ${apiError.data.attemptsRemaining}`,
          );
        } else {
          setError(apiError.message || "Verification failed");
        }
      } else {
        setError("An unexpected error occurred");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleResend = async () => {
    if (!pendingVerification || resendCooldown > 0) return;

    setIsResending(true);
    setError("");
    setSuccess("");

    try {
      const response = await authApi.resendOtp({
        userId: pendingVerification.userId,
        email: pendingVerification.email,
      });

      if (response.success) {
        setSuccess("Verification code sent to your email");
        setResendCooldown(response.data?.retryAfter || 60);
      }
    } catch (err) {
      if (err instanceof AxiosError && err.response?.data) {
        const apiError = err.response.data as ApiResponse<{
          retryAfterSeconds?: number;
        }>;

        // Handle rate limit error gracefully on verify page
        if (err.response.status === 429) {
          const retryAfter = apiError.data?.retryAfterSeconds || 60;
          setError(
            `Too many requests. Please wait ${retryAfter} seconds before trying again.`,
          );
          setResendCooldown(retryAfter);
        } else {
          setError(apiError.message || "Failed to send verification code");
        }
      } else {
        setError("Failed to send verification code");
      }
    } finally {
      setIsResending(false);
    }
  };

  if (!pendingVerification) {
    return null;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Verify your email
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Enter the 6-digit verification code sent to
          </p>
          <p className="text-center text-sm font-medium text-indigo-600">
            {pendingVerification.email}
          </p>
          <p className="mt-2 text-center text-xs text-gray-500">
            Don't see the email? Check your spam folder or click resend below
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="rounded-md bg-red-50 p-4">
              <p className="text-sm text-red-800">{error}</p>
            </div>
          )}

          {success && (
            <div className="rounded-md bg-green-50 p-4">
              <p className="text-sm text-green-800">{success}</p>
            </div>
          )}

          <div>
            <label
              htmlFor="otp"
              className="block text-sm font-medium text-gray-700 text-center mb-2"
            >
              Verification Code
            </label>
            <input
              id="otp"
              name="otp"
              type="text"
              inputMode="numeric"
              pattern="[0-9]*"
              maxLength={6}
              required
              value={otp}
              onChange={(e) => setOtp(e.target.value.replace(/\D/g, ""))}
              className="mt-2 appearance-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 text-center text-2xl tracking-widest font-mono"
              placeholder="000000"
              autoComplete="one-time-code"
            />
          </div>

          <div>
            <button
              type="submit"
              disabled={isLoading || otp.length !== 6}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "Verifying..." : "Verify Email"}
            </button>
          </div>

          <div className="text-center space-y-2">
            <button
              type="button"
              onClick={handleResend}
              disabled={isResending || resendCooldown > 0}
              className="text-sm text-indigo-600 hover:text-indigo-500 disabled:text-gray-400 disabled:cursor-not-allowed font-medium"
            >
              {isResending
                ? "Sending code..."
                : resendCooldown > 0
                  ? `Resend available in ${resendCooldown}s`
                  : "Resend verification code"}
            </button>

            {resendCooldown > 0 && (
              <p className="text-xs text-gray-500">
                Please wait before requesting another code
              </p>
            )}
          </div>
        </form>

        <div className="text-center pt-4 border-t border-gray-200">
          <Link
            to="/login"
            onClick={() => setPendingVerification(null)}
            className="text-sm text-gray-600 hover:text-gray-900"
          >
            ← Back to login
          </Link>
        </div>
      </div>
    </div>
  );
}
