import { signupApi } from "../api/authApi";
import { saveTokens, getAccessToken } from "../utils/tokenStorage";
import { useContext, useState, useEffect } from "react";
import { AuthContext } from "../context/AuthContext";
import { Link, useNavigate } from "react-router-dom";

const SignUpPage = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { setEmail: setAuthEmail, setName: setAuthName } =
    useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    // If user already has a valid access token, redirect to dashboard
    if (getAccessToken()) {
      navigate("/", { replace: true });
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await signupApi({ name, email, password });
      setAuthEmail(response.email);
      setAuthName(response.name);
      saveTokens(response.accessToken, response.refreshToken);

      navigate("/dashboard");
    } catch (error) {
      console.error("Signup failed:", error);
      const response = error.response;

      // Handle 409 Conflict (User Already Exists)
      if (
        response?.status === 409 &&
        response.data?.errorCode === "USER_ALREADY_EXISTS"
      ) {
        setError(
          response.data?.message + " Please Log in instead." ||
            "Email already registered",
        );
      }
      // Handle other API errors
      else if (response?.status) {
        setError(response.data?.message || "Signup failed. Please try again.");
      }
      // Handle network or other errors
      else {
        setError(error.message || "Signup failed. Please try again.");
      }
    }
  };

  return (
    <section className="min-h-screen bg-slate-100 px-4 py-8 sm:py-12">
      <div className="mx-auto w-full max-w-xl rounded-2xl border border-slate-200 bg-white p-6 shadow-[0_22px_45px_-28px_rgba(15,23,42,0.45)] sm:p-10">
        <header className="mb-8 space-y-3">
          <h1 className="text-4xl font-bold leading-tight text-slate-900">
            Create account
            <br />
            Register to start using Nonmus
          </h1>
        </header>

        <form onSubmit={handleSubmit} className="space-y-5">
          <label className="mb-4 flex flex-col gap-2">
            <h1>Name</h1>
            <input
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-base text-slate-800 outline-none transition focus:border-slate-800"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoComplete="name"
              placeholder="Your name"
            />
          </label>

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
              autoComplete="new-password"
              placeholder="Create a password"
            />
          </label>

          <p className="text-red-500">{error}</p>

          <button
            type="submit"
            className="w-full rounded-xl bg-slate-600 py-3 text-lg font-semibold text-white transition hover:bg-slate-400"
          >
            Sign Up
          </button>

          <div className="my-2 flex items-center gap-4 py-2 text-slate-400">
            <span className="h-px flex-1 bg-slate-300" />
            <span className="text-sm">or</span>
            <span className="h-px flex-1 bg-slate-300" />
          </div>

          <button
            type="button"
            className="flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white py-3 text-lg font-semibold text-slate-900 transition hover:bg-slate-50"
          >
            <span className="inline-flex h-6 w-6 items-center justify-center rounded-full border border-slate-300 bg-white text-sm font-bold text-[#4285F4]">
              G
            </span>
            Continue with Google
          </button>

          <p className="pt-3 text-center text-lg text-slate-700">
            Already have an account?{" "}
            <Link
              to="/login"
              className="font-medium text-blue-600 hover:text-blue-700"
            >
              Log in
            </Link>
          </p>
        </form>
      </div>
    </section>
  );
};

export default SignUpPage;
