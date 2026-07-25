import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { loginWithGoogle } from "../utils/GoogleAuthUtil";
import sideLogo from '../../../assets/side-logo.png';
import nonmusLogo from '../../../../public/logo.png';

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setIsLoading(true);

    try {
      await login({ email, password, rememberMe });
    } catch (err) {
      setError(
          err?.response?.data?.message ||
          err.message ||
          "Login failed. Please check your credentials and try again."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <section className="lg:min-h-screen">
        <div className="flex lg:min-h-screen max-w-7xl mx-auto items-center">
          <div className="hidden lg:flex lg:w-1/2 items-center justify-center p-8">
            <img
                src={sideLogo}
                alt="Login Illustration"
                className="w-full h-auto max-w-md object-contain"
            />
          </div>
          <div className="w-full md:w-3/4 md:mx-auto lg:w-1/2 lg:flex lg:justify-center lg:items-center">
            <div className="w-full p-6 lg:w-3/4 md:mx-auto max-w-lg">
              <header className="mb-8">
                <img src={nonmusLogo} className="w-1/3 sm:w-2/5 max-w-xs mx-auto lg:hidden" />
                <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-slate-900">
                  Login to your account
                </h1>
              </header>

              <form
                  onSubmit={handleSubmit}
                  className="space-y-3"
              >
                <div>
                  <label className="mb-2 text-sm sm:text-base lg:text-lg block font-medium text-slate-700">
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
                      className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-slate-700"
                  />
                </div>

                <div>
                  <label className="mb-2 text-sm sm:text-base lg:text-lg block font-medium text-slate-700">
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
                        autoComplete="current-password"
                        placeholder="Your password"
                        required
                        className="w-full rounded-xl border border-slate-300 px-4 py-3 pr-12 outline-none transition focus:border-slate-700"
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
                            <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                            <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                            <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
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
                            <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z" />
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
                    <p className="text-sm text-red-600">
                      {error}
                    </p>
                )}

                <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                  <label className="flex items-center gap-2 text-sm sm:text-base text-slate-700">
                    <input
                        type="checkbox"
                        defaultChecked={rememberMe}
                        className="h-4 w-4 accent-slate-900"
                        onChange={e => setRememberMe(e.target.checked)}
                    />
                    Remember me
                  </label>

                  <button
                      type="button"
                      className="text-sm sm:text-base font-medium text-blue-600 hover:text-blue-700"
                  >
                    Forgot password?
                  </button>
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
                        Logging in...
                      </span>
                  ) : "Log In"}
                </button>

                <div className="flex items-center gap-3">
                  <div className="h-px flex-1 bg-slate-300"></div>
                  <span className="text-sm text-slate-500">or</span>
                  <div className="h-px flex-1 bg-slate-300"></div>
                </div>

                <button
                    type="button"
                    onClick={loginWithGoogle}
                    className="flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white py-3 text-base font-semibold transition hover:bg-slate-50"
                >
                    <span className="flex h-8 w-8 items-center justify-center rounded-full border border-slate-300 font-bold text-[#4285F4]">
                      G
                    </span>
                    Continue with Google
                </button>

                <p className="text-center text-sm text-slate-700 sm:text-base">
                  Don't have an account?{" "}
                  <Link
                      to="/signup"
                      className="font-semibold text-blue-600 hover:text-blue-700"
                  >
                    Sign up
                  </Link>
                </p>
              </form>

            </div>
          </div>
        </div>
      </section>
  );
};

export default LoginPage;