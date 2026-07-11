import {Navigate, useNavigate} from "react-router-dom";
import {useAuth} from "../features/auth/hooks/useAuth.js";

export const PublicRoute = ({children}) => {
    const {user, loading} = useAuth();

    if(loading) {
        return <p>Loading...</p>
    }

    if(!user) {
        return children;
    }

    return <Navigate to="/" replace />;
}