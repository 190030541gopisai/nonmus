import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuth} from "../features/auth/hooks/useAuth";
import LoadingFallback from "./LoadingFallback.jsx";

const PublicRoute = () => {
    const {user, loading, error} = useAuth();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    if (loading) {
        return <LoadingFallback/>;
    }

    if (error) {
        return <Outlet/>;
    }

    if (user) {
        return <Navigate to={from} replace/>;
    }

    return <Outlet/>;
};

export default PublicRoute;