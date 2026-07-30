import {useSearchParams, Link, useNavigate} from "react-router-dom";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import { verifyEmailApi } from "../api/authApi.js";
import {useEffect} from "react";

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
      <div className="flex min-h-screen items-center justify-center bg-slate-50">
        <div className="w-full max-w-sm rounded-xl border border-slate-200 bg-white p-8 text-center shadow-sm">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-red-100">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-red-600">
              <circle cx="12" cy="12" r="10" />
              <line x1="15" y1="9" x2="9" y2="15" />
              <line x1="9" y1="9" x2="15" y2="15" />
            </svg>
          </div>
          <h2 className="mb-2 text-lg font-semibold text-slate-900">Verification Failed</h2>
          <p className="mb-6 text-sm text-slate-600">Invalid verification link.</p>
          <Link to="/" className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-700">Go to Home</Link>
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
    <div className="flex min-h-screen items-center justify-center bg-slate-50">
      <div className="w-full max-w-sm rounded-xl border border-slate-200 bg-white p-8 text-center shadow-sm">
        {isPending && (
          <div>
            <div className="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-blue-600" />
            <p className="text-slate-600">Verifying your email...</p>
          </div>
        )}

        {(isSuccess || alreadyVerified) && (
          <div>
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-green-100">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-green-600">
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </div>
            <h2 className="mb-2 text-lg font-semibold text-slate-900">Email Verified</h2>
            <p className="mb-4 text-sm text-slate-600">{successMessage || alreadyVerifiedSuccessMessage}</p>
            <h1>Redirecting....</h1>
          </div>
        )}

        {isError && !alreadyVerified && (
          <div>
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-red-100">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-red-600">
                <circle cx="12" cy="12" r="10" />
                <line x1="15" y1="9" x2="9" y2="15" />
                <line x1="9" y1="9" x2="15" y2="15" />
              </svg>
            </div>
            <h2 className="mb-2 text-lg font-semibold text-slate-900">Verification Failed</h2>
            <p className="mb-6 text-sm text-slate-600">{errorMessage}</p>
            <Link
              to="/"
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-700"
            >
              Go to Home
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
