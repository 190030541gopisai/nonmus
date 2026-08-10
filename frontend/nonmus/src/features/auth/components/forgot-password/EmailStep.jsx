import {useForm} from "react-hook-form";
import {sendForgotPasswordOtp} from "../../api/forgotPasswordApi.js";
import {useMutation} from "@tanstack/react-query";
import {useEffect, useState} from "react";

const EmailStep = ({defaultEmail, setEmail, setStep}) => {
    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm({
        defaultValues: {email: defaultEmail || ""},
    });

    const [serverError, setServerError] = useState("");

    const {mutate: sendOtpMutate, isPending: isLoading} = useMutation({
        mutationFn: (data) => sendForgotPasswordOtp(data.email),
        onSuccess: (_, variables) => {
            setServerError("");

            setEmail(variables.email);
            setStep(1);
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

    const onSubmit = (data) => sendOtpMutate(data);

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
                    className="w-full border rounded-3xl focus:border-slate-700 border-slate-300 px-4 py-4"
                />
                {errors.email && (
                    <p className="text-red-600 m-4">
                        {errors.email.message}
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
                        Sending code...
                    </span>
                    ) : (
                        "Send Verification Code"
                    )}
                </button>
            </div>
        </form>
    );
};

export default EmailStep;
