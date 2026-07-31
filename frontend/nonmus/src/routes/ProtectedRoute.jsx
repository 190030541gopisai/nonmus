import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuth} from "../features/auth/hooks/useAuth";
import LoadingFallback from "./LoadingFallback.jsx";

const ProtectedRoute = () => {
    const {isAuthenticated, isLoading, error} = useAuth();
    const location = useLocation();

    if (isLoading) {
        return <LoadingFallback/>;
    }

    if (error) {
        return <Navigate to="/login" state={{from: location}} replace/>;
    }

    if (isAuthenticated) {
        return <Outlet/>;
    }

    return <Navigate to="/login" state={{from: location}} replace/>;
};

export default ProtectedRoute;
