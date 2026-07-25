import {Navigate, useLocation} from "react-router-dom";
import {useAuth} from "../features/auth/hooks/useAuth";
import LoadingFallback from "./LoadingFallback.jsx";

const PublicRoute = ({children}) => {
    const {user, loading, error} = useAuth();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    if (loading) {
        return <LoadingFallback/>;
    }

    if (error) {
        return children;
    }

    if (user) {
        return <Navigate to={from} replace/>;
    }

    return children;
};

export default PublicRoute;