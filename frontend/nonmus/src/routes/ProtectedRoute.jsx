import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuth} from "../features/auth/hooks/useAuth";
import LoadingFallback from "./LoadingFallback.jsx";

const ProtectedRoute = () => {
    const {user, loading, error} = useAuth();
    const location = useLocation();

    if (loading) {
        return <LoadingFallback/>;
    }

    if (error) {
        return <Navigate to="/login" state={{from: location}} replace/>;
    }

    if (user) {
        return <Outlet/>;
    }

    return <Navigate to="/login" state={{from: location}} replace/>;
};

export default ProtectedRoute;
