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
      <div >
        <div >
          <div >
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" >
              <circle cx="12" cy="12" r="10" />
              <line x1="15" y1="9" x2="9" y2="15" />
              <line x1="9" y1="9" x2="15" y2="15" />
            </svg>
          </div>
          <h2 >Verification Failed</h2>
          <p >Invalid verification link.</p>
          <Link to="/" >Go to Home</Link>
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
    <div >
      <div >
        {isPending && (
          <div>
            <div />
            <p >Verifying your email...</p>
          </div>
        )}

        {(isSuccess || alreadyVerified) && (
          <div>
            <div >
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </div>
            <h2 >Email Verified</h2>
            <p >{successMessage || alreadyVerifiedSuccessMessage}</p>
            <h1>Redirecting....</h1>
          </div>
        )}

        {isError && !alreadyVerified && (
          <div>
            <div >
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" >
                <circle cx="12" cy="12" r="10" />
                <line x1="15" y1="9" x2="9" y2="15" />
                <line x1="9" y1="9" x2="15" y2="15" />
              </svg>
            </div>
            <h2 >Verification Failed</h2>
            <p >{errorMessage}</p>
            <Link
              to="/"
              >
              Go to Home
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
