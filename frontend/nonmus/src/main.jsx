import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import { AuthProvider } from "./features/auth/context/AuthContext";

// Register the JWT Bearer token interceptor globally before any request fires
import "./api/interceptors.js";

import "./index.css";


createRoot(document.getElementById("root")).render(
  <StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </StrictMode>,
);
