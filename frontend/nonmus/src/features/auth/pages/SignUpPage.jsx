import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {useMutation} from "@tanstack/react-query";
import {loginWithGoogle} from "../utils/GoogleAuthUtil";
import sideLogo from '../../../assets/side-logo.png';
import {signupApi} from "../api/authApi.js";
import {useAuth} from "../hooks/useAuth.js";

const SignUpPage = () => {
    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    const {user, refetchUser} = useAuth();

    if (user) {
        navigate("/", {replace: true});
    }

    const {mutate: handleSignUp, isPending, error} = useMutation({
        mutationFn: (signUpData) => signupApi(signUpData),
        onSuccess: (data) => {
            const message = data?.message;

            if (message === "Signup successful") {
                refetchUser();

                navigate("/", {replace: true});
            }
        }
    })

    const handleSubmit = async (e) => {
        e.preventDefault();
        handleSignUp({name, email, password});
    }

    return (
        <section className="h-screen">
            <div className="h-full flex items-center max-w-7xl mx-auto">
                <div className="hidden lg:block">
                    <img
                        src={sideLogo}
                        alt="Signup Illustration"
                        className="max-w-2xl"
                    />
                </div>
                <>
                    <div className="w-full md:w-1/2 max-w-md md:mx-auto">
                        <header>
                            <img
                                src="/logo.png"
                                className="h-24 w-24 max-w-xs mx-auto"
                            />
                            <h1 className="text-center text-2xl font-bold text-slate-900">
                                Create a Nonmus Account
                            </h1>
                        </header>

                        <form
                            onSubmit={handleSubmit}
                        >
                            <div className="m-4">
                                <label className="block mb-2 font-medium text-slate-700">
                                    Name
                                </label>

                                <input
                                    type="text"
                                    value={name}
                                    onChange={(e) =>
                                        setName(e.target.value)
                                    }
                                    placeholder="Your name"
                                    autoComplete="name"
                                    required
                                    className="w-full border rounded-3xl focus:border-slate-700 border-slate-300 px-4 py-4"
                                />
                            </div>

                            <div className="m-4">
                                <label className="block mb-2 font-medium text-slate-700">
                                    Email
                                </label>

                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) =>
                                        setEmail(e.target.value)
                                    }
                                    placeholder="Your email"
                                    autoComplete="email"
                                    required
                                    className="w-full border rounded-3xl border-slate-300 focus:border-slate-700 px-4 py-4 "
                                />
                            </div>

                            <div className="m-4">
                                <label className="block mb-2 font-medium text-slate-700">
                                    Password
                                </label>

                                <div className="relative">
                                    <input
                                        type={
                                            showPassword ? "text" : "password"
                                        }
                                        value={password}
                                        onChange={(e) =>
                                            setPassword(e.target.value)
                                        }
                                        autoComplete="new-password"
                                        placeholder="Create a password"
                                        required
                                        className="w-full border rounded-3xl border-slate-300 focus:border-slate-700 px-4 py-4 "
                                    />

                                    <button
                                        type="button"
                                        onClick={() =>
                                            setShowPassword(!showPassword)
                                        }
                                        className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-700"
                                    >
                                        {showPassword ? (
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                width="22"
                                                height="22"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                strokeWidth="2"
                                            >
                                                <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"/>
                                                <path
                                                    d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"/>
                                                <path
                                                    d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"/>
                                                <line
                                                    x1="2"
                                                    x2="22"
                                                    y1="2"
                                                    y2="22"
                                                />
                                            </svg>
                                        ) : (
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                width="22"
                                                height="22"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                strokeWidth="2"
                                            >
                                                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/>
                                                <circle
                                                    cx="12"
                                                    cy="12"
                                                    r="3"
                                                />
                                            </svg>
                                        )}
                                    </button>
                                </div>
                            </div>

                            {error && (
                                <p className="text-red-600 m-4">
                                    {error?.response?.data?.errorCode === "USER_ALREADY_EXISTS"
                                        ? error.response.data.message + " Please login instead."
                                        : error?.response?.data?.message || error?.message || "Signup failed."}
                                </p>
                            )}

                            <div className="m-4">
                                <button
                                    type="submit"
                                    disabled={isPending}
                                    className="w-full rounded-xl bg-slate-800 py-3 text-base font-semibold text-white"
                                >
                                    {isPending ? (
                                        <span className="flex items-center justify-center gap-2">
                                        <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24" fill="none">
                                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                                  strokeWidth="4"/>
                                          <path className="opacity-75" fill="currentColor"
                                                d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
                                        </svg>
                                        Creating account...
                                    </span>
                                    ) : "Create Account"}
                                </button>
                            </div>

                            <div className="flex items-center gap-3 m-4">
                                <div className="h-px flex-1 bg-slate-300"></div>
                                <span className="text-slate-500">or</span>
                                <div className="h-px flex-1 bg-slate-300"></div>
                            </div>

                            <div className="m-4">
                                <button
                                    onClick={loginWithGoogle}
                                    type="button"
                                    className="flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white py-3 font-medium text-slate-700 transition hover:bg-slate-50"
                                >
                                    <img
                                        src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                                        alt="Google"
                                        className="h-5 w-5"
                                    />

                                    Sign in with Google
                                </button>
                            </div>

                            <p className="text-center text-sm text-slate-700">
                                Already have an account?{" "}
                                <Link
                                    to="/login"
                                    className="font-semibold text-blue-600 hover:text-blue-700"
                                >
                                    Log in
                                </Link>
                            </p>
                        </form>
                    </div>
                </>
            </div>
        </section>
    );
};

export default SignUpPage;