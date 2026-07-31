import { useForm } from "react-hook-form";

const EmailStep = ({ defaultEmail, onSubmit, isLoading }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: { email: defaultEmail || "" },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div>
        <label className="mb-1.5 block text-sm font-medium text-slate-700">
          Email Address
        </label>
        <input
          type="email"
          autoComplete="email"
          placeholder="Enter your email"
          {...register("email", {
            required: "Email is required",
            pattern: {
              value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
              message: "Enter a valid email address",
            },
          })}
          className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-slate-700"
        />
        {errors.email && (
          <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
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
            Sending code...
          </span>
        ) : (
          "Send Verification Code"
        )}
      </button>
    </form>
  );
};

export default EmailStep;
