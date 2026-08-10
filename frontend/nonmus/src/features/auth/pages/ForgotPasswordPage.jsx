import {useEffect, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useMutation} from "@tanstack/react-query";
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
    {label: "Email", description: "Enter your email"},
    {label: "Verify", description: "Check your inbox"},
    {label: "Reset", description: "Set new password"},
];

const ForgotPasswordPage = () => {
    const [step, setStep] = useState(0);
    const [email, setEmail] = useState("");

    const renderStep = () => {
        switch (step) {
            case 0:
                return (
                    <EmailStep
                        defaultEmail={email}
                        setEmail={setEmail}
                        setStep={setStep}
                    />
                );
            case 1:
                return (
                    <VerifyOtpStep
                        email={email}
                        setStep={setStep}
                    />
                );
            case 2:
                return (
                    <ResetPasswordStep />
                );
            default:
                return null;
        }
    };

    const getHeaderText = () => {
        switch (step) {
            case 0:
                return {title: "Forgot Password", subtitle: "No worries, we'll send you reset instructions."};
            case 1:
                return {title: "Check Your Email", subtitle: `We sent a 6-digit code to ${email}`};
            case 2:
                return {title: "Set New Password", subtitle: "Create a strong password for your account."};
            default:
                return {title: "", subtitle: ""};
        }
    };

    const {title, subtitle} = getHeaderText();

    return (
        <section className="h-screen">
            <div className="h-full flex items-center max-w-7xl mx-auto">
                <div className="hidden lg:block">
                    <img
                        src={sideLogo}
                        alt="Forgot Illustration"
                        className="max-w-2xl"
                    />
                </div>
                <>
                    <div className="w-full max-w-lg md:w-3/4 md:mx-auto ">
                        <header className="m-4">
                            <img
                                src="/logo.png"
                                className="h-24 w-24 max-w-xs mx-auto lg:hidden"
                            />
                            <h1 className="text-2xl font-bold text-slate-900">
                                {title}
                            </h1>
                            <p className="mt-2 text-slate-600">{subtitle}</p>
                        </header>

                        <div className="m-4">
                            <div className="flex items-center justify-between">
                                {STEPS.map((s, i) => (
                                    <div key={s.label} className="flex items-center">
                                        <div className="flex flex-col items-center">
                                            <div className={`h-8 w-8 border rounded-full flex justify-center items-center font-semibold ${i <= step ? 'bg-slate-800': 'bg-slate-500'} text-white`}>
                                                {i + 1}
                                            </div>
                                            <span className={`mt-1 font-medium ${i <= step? 'text-slate-700': 'text-slate-500'}`}>
                                                {s.label}
                                            </span>
                                        </div>
                                        {i < STEPS.length - 1 && (
                                            <div className="mx-2 h-0.5 w-8 bg-slate-800"/>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>

                        {renderStep()}

                        <p className="text-center text-blue-600">
                            <Link
                                to="/login"
                            >
                                Back to login
                            </Link>
                        </p>
                    </div>
                </>
            </div>
        </section>
    );
};

export default ForgotPasswordPage;
