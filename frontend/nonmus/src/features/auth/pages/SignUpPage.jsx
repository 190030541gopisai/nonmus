import { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { loginWithGoogle } from "../utils/GoogleAuthUtil";
import sideLogo from '../../../assets/side-logo.png';

const SignUpPage = () => {
  const navigate = useNavigate();
  const { signup } = useContext(AuthContext);

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    try {
      await signup({
        name,
        email,
        password,
      });

      navigate("/");
    } catch (error) {
      const response = error.response;

      if (
          response?.status === 409 &&
          response?.data?.errorCode === "USER_ALREADY_EXISTS"
      ) {
        setError(
            response.data.message + " Please login instead."
        );
      } else {
        setError(
            response?.data?.message ||
            error.message ||
            "Signup failed."
        );
      }
    }
  };

  return (
      <section className="min-h-screen bg-slate-100">
        <div className="mx-auto flex min-h-screen">

          {/* Left Illustration */}
          <div className="hidden lg:flex lg:w-1/2 items-center justify-center">
            <img
                src={sideLogo}
                alt="Signup Illustration"
                className=" w-full object-contain"
            />
          </div>

          {/* Right Form */}
          <div className="flex w-full items-center justify-center p-4 sm:p-8 lg:w-1/2">

            <div className="w-full max-w-sm rounded-2xl border border-slate-200 bg-white p-6 shadow-xl sm:max-w-md sm:p-8">

              <header className="mb-8">
                <h1 className="text-3xl font-bold text-slate-900 sm:text-4xl">
                  Create your account
                </h1>

                <p className="mt-2 text-slate-500">
                  Register to start using Nonmus.
                </p>
              </header>

              <form
                  onSubmit={handleSubmit}
                  className="space-y-5"
              >
                {/* Name */}

                <div>
                  <label className="mb-2 block font-medium text-slate-700">
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
                      className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-slate-700"
                  />
                </div>

                {/* Email */}

                <div>
                  <label className="mb-2 block font-medium text-slate-700">
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

                {/* Password */}

                <div>
                  <label className="mb-2 block font-medium text-slate-700">
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

                <button
                    type="submit"
                    className="w-full rounded-xl bg-slate-800 py-3 text-base font-semibold text-white transition hover:bg-slate-700 sm:text-lg"
                >
                  Create Account
                </button>

                <div className="flex items-center gap-3">
                  <div className="h-px flex-1 bg-slate-300"></div>
                  <span className="text-sm text-slate-500">
                  or
                </span>
                  <div className="h-px flex-1 bg-slate-300"></div>
                </div>

                <button
                    type="button"
                    onClick={loginWithGoogle}
                    className="flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white py-3 text-base font-semibold transition hover:bg-slate-50 sm:text-lg"
                >
                <span className="flex h-8 w-8 items-center justify-center rounded-full border border-slate-300 font-bold text-[#4285F4]">
                  G
                </span>

                  Continue with Google
                </button>

                <p className="text-center text-sm text-slate-700 sm:text-base">
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
          </div>
        </div>
      </section>
  );
};

export default SignUpPage;