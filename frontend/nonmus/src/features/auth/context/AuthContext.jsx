import { createContext, useEffect, useState } from "react";
import { meApi } from "../api/userApi";
import {refreshApi} from "../api/authApi.js";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

    const fetchUser = async () => {
        try {
            const userData = await meApi();
            setUser(userData);
        } catch (err) {
            try {
                await refreshApi();
                const userData = await meApi();
                setUser(userData);
            } catch(retryErr) {
                setUser(null);
                setError(retryErr);
            }
        } finally {
            setLoading(false);
        }
    };

  useEffect(() => {
    fetchUser();
  }, []);

  return (
      <AuthContext.Provider
          value={{
            user,
            setUser,
            loading,
            error
          }}
      >
        {children}
      </AuthContext.Provider>
  );
}