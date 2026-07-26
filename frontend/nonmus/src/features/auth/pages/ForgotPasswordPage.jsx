import { useState, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  sendForgotPasswordOtp,
  verifyForgotPasswordOtp,
  resetForgotPassword,
} from "../api/forgotPasswordApi";
import EmailStep from "../components/forgot-password/EmailStep";
import VerifyOtpStep from "../components/forgot-password/VerifyOtpStep";
import ResetPasswordStep from "../components/forgot-password/ResetPasswordStep";
import sideLogo from "../../../assets/side-logo.png";


const STEPS = [
  { label: "Email", description: "Enter your email" },
  { label: "Verify", description: "Check your inbox" },
  { label: "Reset", description: "Set new password" },
];

const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(0);
  const [email, setEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const [serverError, setServerError] = useState("");

  const handleSendOtp = useCallback(async (data) => {
    setServerError("");
    setIsLoading(true);
    try {
      await sendForgotPasswordOtp(data.email);
      setEmail(data.email);
      setStep(1);
    } catch (err) {
      setServerError(
        err?.response?.data?.message ||
        err.message ||
        "Failed to send verification code. Please try again."
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  const handleVerifyOtp = useCallback(async (data) => {
    setServerError("");
    setIsLoading(true);
    try {
      await verifyForgotPasswordOtp(email, data.code);
      setStep(2);
    } catch (err) {
      const status = err?.response?.status;
      if (status === 410) {
        setServerError("Verification code has expired. Please request a new one.");
      } else if (status === 400) {
        setServerError("Invalid verification code. Please try again.");
      } else {
        setServerError(
          err?.response?.data?.message ||
          err.message ||
          "Verification failed. Please try again."
        );
      }
    } finally {
      setIsLoading(false);
    }
  }, [email]);

  const handleResendOtp = useCallback(async () => {
    setServerError("");
    setIsResending(true);
    try {
      await sendForgotPasswordOtp(email);
    } catch (err) {
      setServerError(
        err?.response?.data?.message ||
        err.message ||
        "Failed to resend code. Please try again."
      );
    } finally {
      setIsResending(false);
    }
  }, [email]);

  const handleResetPassword = useCallback(async (data) => {
    setServerError("");
    setIsLoading(true);
    try {
      await resetForgotPassword(data.newPassword);
      navigate("/login", {
        state: { message: "Password reset successful. Please log in." },
      });
    } catch (err) {
      const status = err?.response?.status;
      if (status === 410) {
        setServerError("Reset link has expired. Please start over.");
      } else {
        setServerError(
          err?.response?.data?.message ||
          err.message ||
          "Failed to reset password. Please try again."
        );
      }
    } finally {
      setIsLoading(false);
    }
  }, [navigate]);

  const renderStep = () => {
    switch (step) {
      case 0:
        return (
          <EmailStep
            defaultEmail={email}
            onSubmit={handleSendOtp}
            isLoading={isLoading}
          />
        );
      case 1:
        return (
          <VerifyOtpStep
            email={email}
            onSubmit={handleVerifyOtp}
            onResend={handleResendOtp}
            isLoading={isLoading}
            isResending={isResending}
            serverError={serverError}
          />
        );
      case 2:
        return (
          <ResetPasswordStep
            onSubmit={handleResetPassword}
            isLoading={isLoading}
            serverError={serverError}
          />
        );
      default:
        return null;
    }
  };

  const getHeaderText = () => {
    switch (step) {
      case 0:
        return { title: "Forgot Password", subtitle: "No worries, we'll send you reset instructions." };
      case 1:
        return { title: "Check Your Email", subtitle: `We sent a 6-digit code to ${email}` };
      case 2:
        return { title: "Set New Password", subtitle: "Create a strong password for your account." };
      default:
        return { title: "", subtitle: "" };
    }
  };

  const { title, subtitle } = getHeaderText();

  return (
    <section className="lg:min-h-screen">
      <div className="flex lg:min-h-screen max-w-7xl mx-auto items-center">
        <div className="hidden lg:flex lg:w-1/2 items-center justify-center p-8">
          <img
            src={sideLogo}
            alt="Forgot password illustration"
            className="w-full h-auto max-w-md object-contain"
          />
        </div>
        <div className="w-full md:w-3/4 md:mx-auto lg:w-1/2 lg:flex lg:justify-center lg:items-center">
          <div className="w-full p-6 lg:w-3/4 md:mx-auto max-w-lg">
            <header className="mb-8">
              <img src="/logo.png" className="w-1/3 sm:w-2/5 max-w-xs mx-auto lg:hidden" />
              <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-slate-900">
                {title}
              </h1>
              <p className="mt-2 text-sm text-slate-600">{subtitle}</p>
            </header>

            <div className="mb-8">
              <div className="flex items-center justify-between">
                {STEPS.map((s, i) => (
                  <div key={s.label} className="flex items-center">
                    <div className="flex flex-col items-center">
                      <div
                        className={`flex h-8 w-8 items-center justify-center rounded-full text-sm font-semibold transition-colors ${
                          i <= step
                            ? "bg-slate-800 text-white"
                            : "bg-slate-200 text-slate-500"
                        }`}
                      >
                        {i + 1}
                      </div>
                      <span
                        className={`mt-1 text-xs font-medium ${
                          i <= step ? "text-slate-700" : "text-slate-400"
                        }`}
                      >
                        {s.label}
                      </span>
                    </div>
                    {i < STEPS.length - 1 && (
                      <div
                        className={`mx-2 h-0.5 w-8 sm:w-12 transition-colors ${
                          i < step ? "bg-slate-800" : "bg-slate-200"
                        }`}
                      />
                    )}
                  </div>
                ))}
              </div>
            </div>

            {renderStep()}

            <p className="mt-6 text-center text-sm text-slate-700">
              <Link
                to="/login"
                className="font-semibold text-blue-600 hover:text-blue-700"
              >
                Back to login
              </Link>
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default ForgotPasswordPage;
