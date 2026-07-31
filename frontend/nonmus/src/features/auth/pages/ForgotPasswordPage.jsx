import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
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

  const sendOtpMutation = useMutation({
    mutationFn: (data) => sendForgotPasswordOtp(data.email),
    onSuccess: (_, variables) => {
      setEmail(variables.email);
      setStep(1);
    },
  });

  const verifyOtpMutation = useMutation({
    mutationFn: (data) => verifyForgotPasswordOtp(email, data.code),
    onSuccess: () => setStep(2),
  });

  const resendOtpMutation = useMutation({
    mutationFn: () => sendForgotPasswordOtp(email),
  });

  const resetPasswordMutation = useMutation({
    mutationFn: (data) => resetForgotPassword(data.newPassword),
    onSuccess: () => {
      navigate("/login", {
        state: { message: "Password reset successful. Please log in." },
      });
    },
  });

  const getError = (mutation) => {
    const err = mutation.error;
    if (!err) return "";
    const status = err?.response?.status;
    if (status === 410) return "Verification code has expired. Please request a new one.";
    if (status === 400) return "Invalid verification code. Please try again.";
    return err?.response?.data?.message || err.message || "Something went wrong. Please try again.";
  };

  const handleSendOtp = (data) => sendOtpMutation.mutate(data);
  const handleVerifyOtp = (data) => verifyOtpMutation.mutate(data);
  const handleResendOtp = () => resendOtpMutation.mutate();
  const handleResetPassword = (data) => resetPasswordMutation.mutate(data);

  const renderStep = () => {
    switch (step) {
      case 0:
        return (
          <EmailStep
            defaultEmail={email}
            onSubmit={handleSendOtp}
            isLoading={sendOtpMutation.isPending}
          />
        );
      case 1:
        return (
          <VerifyOtpStep
            email={email}
            onSubmit={handleVerifyOtp}
            onResend={handleResendOtp}
            isLoading={verifyOtpMutation.isPending}
            isResending={resendOtpMutation.isPending}
            serverError={getError(verifyOtpMutation) || getError(resendOtpMutation)}
          />
        );
      case 2:
        return (
          <ResetPasswordStep
            onSubmit={handleResetPassword}
            isLoading={resetPasswordMutation.isPending}
            serverError={getError(resetPasswordMutation)}
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
