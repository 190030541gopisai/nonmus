import {
  createContext,
  useContext,
  useState,
  useEffect,
  type ReactNode,
} from "react";
import type { UserInfo, TokenInfo } from "../types";

interface AuthState {
  user: UserInfo | null;
  tokens: TokenInfo | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

interface PendingVerification {
  userId: string;
  email: string;
}

interface AuthContextType extends AuthState {
  login: (user: UserInfo, tokens: TokenInfo) => void;
  logout: () => void;
  setPendingVerification: (data: PendingVerification | null) => void;
  pendingVerification: PendingVerification | null;
}

const AuthContext = createContext<AuthContextType | null>(null);

const STORAGE_KEYS = {
  USER: "nonmus_user",
  TOKENS: "nonmus_tokens",
};

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [tokens, setTokens] = useState<TokenInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [pendingVerification, setPendingVerification] =
    useState<PendingVerification | null>(null);

  useEffect(() => {
    const storedUser = localStorage.getItem(STORAGE_KEYS.USER);
    const storedTokens = localStorage.getItem(STORAGE_KEYS.TOKENS);

    if (storedUser && storedTokens) {
      setUser(JSON.parse(storedUser));
      setTokens(JSON.parse(storedTokens));
    }
    setIsLoading(false);
  }, []);

  const login = (user: UserInfo, tokens: TokenInfo) => {
    setUser(user);
    setTokens(tokens);
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
    localStorage.setItem(STORAGE_KEYS.TOKENS, JSON.stringify(tokens));
  };

  const logout = () => {
    setUser(null);
    setTokens(null);
    localStorage.removeItem(STORAGE_KEYS.USER);
    localStorage.removeItem(STORAGE_KEYS.TOKENS);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        tokens,
        isAuthenticated: !!user && !!tokens,
        isLoading,
        login,
        logout,
        pendingVerification,
        setPendingVerification,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
