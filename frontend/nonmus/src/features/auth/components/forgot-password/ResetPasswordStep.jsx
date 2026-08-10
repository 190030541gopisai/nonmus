import {useEffect, useState} from "react";
import {useForm} from "react-hook-form";
import {useMutation} from "@tanstack/react-query";
import {resetForgotPassword} from "../../api/forgotPasswordApi.js";
import {useNavigate} from "react-router-dom";

const EyeIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         strokeWidth="2">
        <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/>
        <circle cx="12" cy="12" r="3"/>
    </svg>
);

const EyeOffIcon = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         strokeWidth="2">
        <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"/>
        <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"/>
        <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"/>
        <line x1="2" x2="22" y1="2" y2="22"/>
    </svg>
);

const ResetPasswordStep = () => {
    const navigate = useNavigate();

    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [serverError, setServerError] = useState("");

    const {
        register,
        handleSubmit,
        watch,
        formState: {errors},
    } = useForm({
        defaultValues: {newPassword: "", confirmPassword: ""},
    });

    const {mutate: resetPasswordMutate, isPending: isLoading} = useMutation({
        mutationFn: (data) => resetForgotPassword(data.newPassword),
        onSuccess: () => {
            navigate("/login", {
                state: {message: "Password reset successful. Please log in."},
            });
        },
        onError: (err) => {
            const getError = () => {
                const status = err?.response?.status;
                if (status === 410) return "Verification code has expired. Please request a new one.";
                if (status === 400) return "Session invalid. Please restart from beginning.";
                if (status === 500) return "Something went wrong. Please restart from beginning.";
                return err?.response?.data?.message || err.message || "Something went wrong. Please try again.";
            }

            setServerError(getError());
        }
    });

    const onSubmit = (data) => resetPasswordMutate(data);

    const newPassword = watch("newPassword");

    useEffect(() => {
        let timeout;
        if(serverError) {
            timeout = setTimeout(() => setServerError(""), 4000);
        }

        return () => {
            clearTimeout(timeout);
        }
    }, [serverError]);

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            {serverError && (
                <div className="border border-red-200 bg-red-50 rounded-xl m-4 px-4 py-3 text-red-700">
                    {serverError}
                </div>
            )}


            <div className="m-4">
                <label className="block mb-2 font-medium text-slate-700">
                    New Password
                </label>
                <div className="relative">
                    <input
                        type={showPassword ? "text" : "password"}
                        autoComplete="new-password"
                        placeholder="Enter new password"
                        {...register("newPassword", {
                            required: "Password is required",
                            minLength: {
                                value: 6,
                                message: "Password must be at least 6 characters",
                            },
                        })}
                        className="w-full border rounded-3xl border-slate-300 focus:border-slate-700 px-4 py-4 "
                    />
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-700"
                    >
                        {showPassword ? <EyeIcon/>: <EyeOffIcon/> }
                    </button>
                </div>
                {errors.newPassword && (
                    <p className="m-4 text-red-700">
                        {errors.newPassword.message}
                    </p>
                )}
            </div>

            <div className="m-4">
                <label className="block mb-2 font-medium text-slate-700">
                    Confirm Password
                </label>
                <div className="relative">
                    <input
                        type={showConfirm ? "text" : "password"}
                        autoComplete="new-password"
                        placeholder="Confirm new password"
                        {...register("confirmPassword", {
                            required: "Please confirm your password",
                            validate: (value) =>
                                value === newPassword || "Passwords do not match",
                        })}
                        className="w-full border rounded-3xl border-slate-300 focus:border-slate-700 px-4 py-4 "
                    />
                    <button
                        type="button"
                        onClick={() => setShowConfirm(!showConfirm)}
                        className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-700"
                    >
                        {showConfirm ? <EyeIcon/>: <EyeOffIcon/>}
                    </button>
                </div>
                {errors.confirmPassword && (
                    <p className="m-4 text-red-700">
                        {errors.confirmPassword.message}
                    </p>
                )}
            </div>

            <div className="m-4">
                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full rounded-xl bg-slate-800 py-3 text-base font-semibold text-white"
                >
                    {isLoading ? (
                        <span className="flex items-center justify-center gap-2">
                            <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
                              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
                            </svg>
                            Resetting password...
                        </span>
                    ) : (
                        "Reset Password"
                    )}
                </button>
            </div>
        </form>
    );
};

export default ResetPasswordStep;
