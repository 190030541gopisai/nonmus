import {useForm} from "react-hook-form";
import {useMutation} from "@tanstack/react-query";
import {sendForgotPasswordOtp, verifyForgotPasswordOtp} from "../../api/forgotPasswordApi.js";
import {useEffect, useState} from "react";

const VerifyOtpStep = ({ email, setStep }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: { code: "" },
  });

  const [resendSuccessMessage, setResendSuccessMessage] = useState("");
  const [serverError, setServerError] = useState("");

    const {mutate: verifyOtpMutate, isPending: isLoading} = useMutation({
        mutationFn: (data) => verifyForgotPasswordOtp(email, data.code),
        onSuccess: () => {
            setStep(2);
            setServerError("");
        },
        onError: (err) => {
            const getError = () => {
                const status = err?.response?.status;
                if (status === 410) return "Verification code has expired. Please request a new one.";
                if (status === 400) return "Invalid verification code. Please try again.";
                return err?.response?.data?.message || err.message || "Something went wrong. Please try again.";
            }

            setServerError(getError());
        }
    });

    const {mutate: resendOtpMutate, isPending: isResending} = useMutation({
        mutationFn: () => sendForgotPasswordOtp(email),
        onSuccess: (data) => {
            setServerError("");
            setResendSuccessMessage(data?.message || "Otp resent successful");
        },
        onError: (err) => {
            const getError = () => {
                const status = err?.response?.status;
                if (status === 410) return "Verification code has expired. Please request a new one.";
                if (status === 400) return "Invalid verification code. Please try again.";
                return err?.response?.data?.message || err.message || "Something went wrong. Please try again.";
            }

            setServerError(getError());
        }
    });

    const onSubmit = (data) => verifyOtpMutate(data);
    const onResend = () => resendOtpMutate();

    useEffect(() => {
        let timeout;
        if(serverError) {
            timeout = setTimeout(() => setServerError(""), 4000);
        }

        return () => {
            clearTimeout(timeout);
        }
    }, [serverError]);

    useEffect(() => {
        let timeout;

        if(resendSuccessMessage) {
            timeout = setTimeout(() => setResendSuccessMessage(""), 4000);
        }

        return () => {
            clearTimeout(timeout);
        }

    }, [resendSuccessMessage]);

    return (
    <form onSubmit={handleSubmit(onSubmit)} >
      {serverError && (
        <div className="border border-red-200 bg-red-50 rounded-xl m-4 px-4 py-3 text-red-700">
          {serverError}
        </div>
      )}

        {
            resendSuccessMessage && (
                <div className="border border-green-200 bg-green-50 rounded-xl m-4 px-4 py-3 text-green-700">
                    {resendSuccessMessage}
                </div>
            )
        }

      <div className="m-4">
        <p className="mb-4 text-sm text-slate-600">
          Enter the 6-digit code sent to <strong>{email}</strong>
        </p>

        <label className="mb-1.5 block text-sm font-medium text-slate-700">
          Verification Code
        </label>
        <input
          type="text"
          inputMode="numeric"
          autoComplete="one-time-code"
          maxLength={6}
          placeholder="000000"
          {...register("code", {
            required: "Verification code is required",
            pattern: {
              value: /^\d{6}$/,
              message: "Code must be exactly 6 digits",
            },
          })}
          className="w-full rounded-xl border border-slate-300 px-4 py-3 text-center text-2xl tracking-[0.5em] outline-none transition focus:border-slate-700"
        />
        {errors.code && (
          <p className="mt-1 text-sm text-red-600">{errors.code.message}</p>
        )}
      </div>

      <div className="m-4">
          <button
              type="submit"
              disabled={isLoading}
              className="w-full rounded-xl bg-slate-800 py-3 text-base font-semibold text-white transition hover:bg-slate-700 disabled:opacity-60"
          >
              {isLoading ? (
                  <span className="flex items-center justify-center gap-2">
            <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
            </svg>
            Verifying...
          </span>
              ) : (
                  "Verify Code"
              )}
          </button>
      </div>

      <div className="m-4">
        <button
          type="button"
          onClick={onResend}
          disabled={isResending}
          className="w-full bg-slate-800 rounded-xl py-3 text-white"
          >
          {isResending ? (
              <span className="flex items-center justify-center gap-2">
                <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
                </svg>
                Resending...
              </span>
          ) : (
            "Resend code"
          )}
        </button>
      </div>
    </form>
  );
};

export default VerifyOtpStep;
