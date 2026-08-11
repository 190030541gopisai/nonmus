import {useSearchParams, Link, useNavigate} from "react-router-dom";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import { verifyEmailApi } from "../api/authApi.js";
import {useEffect} from "react";

function IconCircle({children, tone = "bg-slate-100 text-slate-500"}) {
  return (
    <div className={`mx-auto flex h-16 w-16 items-center justify-center rounded-2xl ${tone}`}>
      {children}
    </div>
  );
}

function ErrorIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" />
      <line x1="15" y1="9" x2="9" y2="15" />
      <line x1="9" y1="9" x2="15" y2="15" />
    </svg>
  );
}

function CheckIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="20 6 9 17 4 12" />
    </svg>
  );
}

export default function VerifyEmailPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const {data, isPending, isSuccess, isError, error} = useQuery({
    queryKey: ["verifyEmail", token],
    queryFn: () => verifyEmailApi(token),
    enabled: !!token,
    retry: false,
    refetchOnWindowFocus: false
  });

  if (!token) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-100 via-white to-indigo-100 px-4">
        <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-xl shadow-slate-200/60">
          <IconCircle tone="bg-red-50 text-red-500">
            <ErrorIcon />
          </IconCircle>
          <h2 className="mt-6 text-2xl font-bold text-slate-900">Verification Failed</h2>
          <p className="mt-3 text-sm text-slate-500">Invalid verification link.</p>
          <Link
            to="/"
            className="mt-6 inline-flex items-center justify-center rounded-xl bg-slate-800 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-700"
          >
            Go to Home
          </Link>
        </div>
      </div>
    );
  }

  const successMessage = data?.message;
  const alreadyVerified = error?.response?.data?.errorCode === "EMAIL_ALREADY_VERIFIED";
  const alreadyVerifiedSuccessMessage = alreadyVerified && "Email Already Verified";

  const errorMessage = error?.response?.data?.message || "Verification failed. The link may have expired.";

  useEffect(() => {
    if (successMessage || alreadyVerified) {
      const timeout = setTimeout(() => {
        queryClient.invalidateQueries("user");
        navigate("/", { replace: true });
      }, 3000);

      return () => clearTimeout(timeout);
    }
  }, [successMessage, alreadyVerified, navigate]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-100 via-white to-emerald-50 px-4">
      <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-xl shadow-slate-200/60">
        {isPending && (
          <div className="flex flex-col items-center gap-4">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-blue-600" />
            <p className="text-sm font-medium text-slate-500">Verifying your email...</p>
          </div>
        )}

        {(isSuccess || alreadyVerified) && (
          <div>
            <IconCircle tone="bg-emerald-50 text-emerald-500">
              <CheckIcon />
            </IconCircle>
            <h2 className="mt-6 text-2xl font-bold text-slate-900">Email Verified</h2>
            <p className="mt-3 text-sm text-slate-500">{successMessage || alreadyVerifiedSuccessMessage}</p>
            <p className="mt-6 inline-flex items-center gap-2 text-sm font-medium text-slate-400">
              <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
              </svg>
              Redirecting....
            </p>
          </div>
        )}

        {isError && !alreadyVerified && (
          <div>
            <IconCircle tone="bg-red-50 text-red-500">
              <ErrorIcon />
            </IconCircle>
            <h2 className="mt-6 text-2xl font-bold text-slate-900">Verification Failed</h2>
            <p className="mt-3 text-sm text-slate-500">{errorMessage}</p>
            <Link
              to="/"
              className="mt-6 inline-flex items-center justify-center rounded-xl bg-slate-800 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-700"
            >
              Go to Home
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}