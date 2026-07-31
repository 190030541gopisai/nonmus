import { useForm } from "react-hook-form";

const VerifyOtpStep = ({ email, onSubmit, onResend, isLoading, isResending, serverError }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: { code: "" },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {serverError && (
        <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {serverError}
        </div>
      )}

      <div>
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

      <div className="text-center">
        <button
          type="button"
          onClick={onResend}
          disabled={isResending}
          className="text-sm font-medium text-blue-600 hover:text-blue-700 disabled:opacity-60"
        >
          {isResending ? (
            <span className="flex items-center justify-center gap-2">
              <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
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
