import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useAuth } from "../hooks/useAuth.js";
import { resendVerificationEmailApi } from "../api/authApi.js";

export default function EmailVerificationBanner() {
  const { user } = useAuth();
  const [sent, setSent] = useState(false);

  const {mutate: handleResend, isPending: sending, error} = useMutation({
    mutationFn: () => resendVerificationEmailApi(),
    onSuccess: () => setSent(true),
  });

  if (!user || user.emailVerified) return null;

  return (
      <div className="border-b border-amber-200 bg-amber-50">
        <div className="flex flex-col md:flex-row lg:max-w-3/4 lg:mx-auto items-center justify-between gap-4 px-4 py-3 sm:px-6">
          <div className="flex items-center gap-2 text-sm text-amber-800">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
              <line x1="12" y1="9" x2="12" y2="13" />
              <line x1="12" y1="17" x2="12.01" y2="17" />
            </svg>
            <span>
              Your email is not verified. Please verify your email to access all features.
            </span>
          </div>
          <div className="flex items-center gap-3 shrink-0">
            {sent && (
              <span className="text-sm text-green-600 font-medium">Verification email sent!</span>
            )}
            {error && (
              <span className="text-sm text-red-600">{error}</span>
            )}
            <button
              type="button"
              onClick={handleResend}
              disabled={sending}
              className="rounded-lg bg-amber-100 px-3 py-1.5 text-sm font-medium text-amber-800 transition hover:bg-amber-200 disabled:opacity-50"
            >
              {sending ? "Sending..." : "Resend verification email"}
            </button>
          </div>
        </div>
      </div>
  );
}
