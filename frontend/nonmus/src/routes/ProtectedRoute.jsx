import { Navigate } from "react-router-dom";
import { getAccessToken } from "../features/auth/utils/tokenStorage";
import { useAuth } from "../features/auth/hooks/useAuth";

const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (user) {
    return children;
  }

  return <Navigate to="/login" replace />;
};

export default ProtectedRoute;
