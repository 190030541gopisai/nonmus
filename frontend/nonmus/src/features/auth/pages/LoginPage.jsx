import { loginApi } from "../api/authApi";
import {useContext, useState} from "react";
import { Link, useNavigate } from "react-router-dom";
import {loginWithGoogle} from "../utils/GoogleUtil.js";

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await loginApi({ email, password });

      const message = response?.message;

      if(message == "Login Successfull") {
        navigate("/")
      }
    } catch (error) {
      console.error("Login failed:", error);
      setError("Login failed. Please check your credentials and try again.");
    }
  };

  return (
    <section className="min-h-screen bg-slate-100 px-4 py-8 sm:py-12">
      <div className="mx-auto w-full max-w-xl rounded-2xl border border-slate-200 bg-white p-6 shadow-[0_22px_45px_-28px_rgba(15,23,42,0.45)] sm:p-10">
        <header className="mb-8 space-y-3">
          <h1 className="text-4xl font-bold leading-tight text-slate-900">
            Welcome back!
            <br />
            Login to your account
          </h1>
        </header>

        <form onSubmit={handleSubmit} className="space-y-5">
          <label className="mb-4 flex flex-col gap-2">
            <h1>Email</h1>
            <input
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-base text-slate-800 outline-none transition focus:border-slate-800"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
              placeholder="Your email"
            />
          </label>

          <label className="mb-4 flex flex-col gap-2">
            <h1>Password</h1>
            <input
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-base text-slate-800 outline-none transition focus:border-slate-800"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
              placeholder="Your password"
            />
          </label>

          {error && <p className="text-sm text-red-600">{error}</p>}

          <button
            type="submit"
            className="w-full rounded-xl bg-slate-600 py-3 text-lg font-semibold text-white transition hover:bg-slate-400"
          >
            Log In
          </button>

          <div className="flex items-center justify-between pt-1">
            <label
              htmlFor="remember"
              className="flex items-center gap-3 text-slate-700"
            >
              <input
                type="checkbox"
                id="remember"
                defaultChecked
                className="h-5 w-5 accent-slate-900"
              />
              <span>Remember me</span>
            </label>

            <button
              type="button"
              className="font-medium text-blue-600 transition hover:text-blue-700"
            >
              Forgot password?
            </button>
          </div>

          <div className="my-2 flex items-center gap-4 py-2 text-slate-400">
            <span className="h-px flex-1 bg-slate-300" />
            <span className="text-sm">or</span>
            <span className="h-px flex-1 bg-slate-300" />
          </div>

          <button
              onClick={loginWithGoogle}
            type="button"
            className="flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white py-3 text-lg font-semibold text-slate-900 transition hover:bg-slate-50"
          >
            <span className="inline-flex h-6 w-6 items-center justify-center rounded-full border border-slate-300 bg-white text-sm font-bold text-[#4285F4]">
              G
            </span>
            Continue with Google
          </button>

          <p className="pt-3 text-center text-lg text-slate-700">
            Don&apos;t have an account?{" "}
            <Link
              to="/signup"
              className="font-medium text-blue-600 hover:text-blue-700"
            >
              Sign up
            </Link>
          </p>
        </form>
      </div>
    </section>
  );
};

export default LoginPage;
