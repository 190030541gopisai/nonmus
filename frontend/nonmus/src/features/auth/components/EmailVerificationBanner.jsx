import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useAuth } from "../hooks/useAuth.js";
import { resendVerificationEmailApi } from "../api/authApi.js";
import {IoClose} from "react-icons/io5";

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
        <div className="flex justify-center items-center gap-3">
          {sent && (
              <span className="text-sm font-medium text-emerald-600">Verification email sent!</span>
          )}
          {error && (
              <span className="text-sm text-red-600">{error}</span>
          )}
          {(sent || error) && (
              <button
                  type="button"
                  onClick={() => {
                    setSent(false);
                  }}
                  className="font-medium text-gray-500 hover:text-gray-700"
                  aria-label="Dismiss message"
              >
                <IoClose />
              </button>
          )}
        </div>
        <div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-x-4 gap-y-2 px-4 py-3">
          <div className="flex items-center gap-2 text-sm text-amber-800">
            <div className="text-amber-500">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
                <line x1="12" y1="9" x2="12" y2="13" />
                <line x1="12" y1="17" x2="12.01" y2="17" />
              </svg>
            </div>
            <span>
              Your email is not verified. Please verify your email to access all features.
            </span>
          </div>
          <div>
            <button
              type="button"
              onClick={handleResend}
              disabled={sending}
              className="rounded-lg border border-amber-300 bg-white px-3 py-1.5 text-xs font-semibold text-amber-800 transition hover:bg-amber-100 disabled:cursor-not-allowed disabled:opacity-60"
              >
              {sending ? "Sending..." : "Resend verification email"}
            </button>
          </div>
        </div>
      </div>
  );
}