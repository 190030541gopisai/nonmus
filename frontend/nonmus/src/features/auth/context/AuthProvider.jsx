import {useCallback, useEffect, useRef, useState} from "react";
import {AuthContext} from "./authContext.js";
import * as authService from "../services/authService.js";

export function AuthProvider({children}) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const fetchIdRef = useRef(0);

    useEffect(() => {
        const id = ++fetchIdRef.current;

        authService.fetchCurrentUser()
            .then((userData) => {
                if (id !== fetchIdRef.current) return;
                setUser(userData);
                setError(null);
                setLoading(false);
            })
            .catch((err) => {
                if (id !== fetchIdRef.current) return;
                setUser(null);
                setError(err);
                setLoading(false);
            });
    }, []);

    const fetchUser = useCallback(async () => {
        const id = ++fetchIdRef.current;

        try {
            const userData = await authService.fetchCurrentUser();
            if (id !== fetchIdRef.current) return;
            setUser(userData);
            setError(null);
        } catch (err) {
            if (id !== fetchIdRef.current) return;
            setUser(null);
            setError(err);
        }
    }, []);

    const login = useCallback(async (credentials) => {
        await authService.login(credentials);
        await fetchUser();
    }, [fetchUser]);

    const signup = useCallback(async (data) => {
        await authService.signup(data);
        await fetchUser();
    }, [fetchUser]);

    const logout = useCallback(async () => {
        try {
            await authService.logout();
        } finally {
            setUser(null);
            setError(null);
        }
    }, []);

    return (
        <AuthContext.Provider value={{user, loading, error, login, signup, logout, fetchUser}}>
            {children}
        </AuthContext.Provider>
    );
}
