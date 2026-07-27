const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
const OAUTH_BASE = API_URL.replace("/api", "");

export const loginWithGoogle = () => {
    window.location.href = `${OAUTH_BASE}/oauth2/authorization/google`;
}