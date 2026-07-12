import {createContext, useCallback, useEffect, useState} from "react";
import {meApi} from "../api/userApi";
import {loginApi, logoutApi, refreshApi, signupApi} from "../api/authApi.js";

export const AuthContext = createContext();

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchUser = useCallback(async () => {
        try {
            setError(null);
            const userData = await meApi();
            setUser(userData);
        } catch (err) {
            try {
                await refreshApi();
                const userData = await meApi();
                setUser(userData);
            } catch (retryErr) {
                setUser(null);
                setError(retryErr);
            }
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchUser();
    }, [fetchUser]);

    const login = async (credentials) => {
        setLoading(true);
        try {
            await loginApi(credentials);
            await fetchUser();
        } finally {
            setLoading(false);
        }
    };

    const signup = async (data) => {
        setLoading(true);
        try {
            await signupApi(data);
            await fetchUser();
        } finally {
            setLoading(false);
        }
    };

    const logout = async () => {
        try {
            await logoutApi();
        } finally {
            setUser(null);
            setError(null);
        }
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                error,
                login,
                signup,
                logout
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}