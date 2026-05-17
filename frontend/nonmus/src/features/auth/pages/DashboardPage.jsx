import { clearTokens } from "../utils/tokenStorage";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const DashboardPage = () => {
  const { email, name } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    clearTokens();
    navigate("/login", { replace: true });
  };

  return (
    <main
      style={{
        minHeight: "100vh",
        display: "grid",
        placeItems: "center",
        padding: "24px",
        background:
          "linear-gradient(135deg, #020617 0%, #0f172a 45%, #111827 100%)",
        color: "#e5e7eb",
      }}
    >
      <section
        style={{
          width: "100%",
          maxWidth: "560px",
          padding: "32px",
          borderRadius: "28px",
          background: "rgba(15, 23, 42, 0.84)",
          border: "1px solid rgba(148, 163, 184, 0.18)",
          boxShadow: "0 28px 90px rgba(0, 0, 0, 0.4)",
          backdropFilter: "blur(18px)",
        }}
      >
        <p
          style={{
            margin: 0,
            color: "#a7f3d0",
            textTransform: "uppercase",
            letterSpacing: "0.12em",
            fontSize: "0.8rem",
          }}
        >
          Signed in
        </p>
        <h1 style={{ margin: "10px 0 8px", fontSize: "2.2rem" }}>
          Welcome{name ? `, ${name}` : ""}
        </h1>
        <p style={{ margin: 0, color: "#cbd5e1", lineHeight: 1.6 }}>
          {email
            ? `You are logged in as ${email}.`
            : "Your session is active and protected routes are working."}
        </p>

        <button
          type="button"
          onClick={handleLogout}
          style={{
            marginTop: "24px",
            border: "none",
            borderRadius: "14px",
            padding: "14px 18px",
            fontWeight: 700,
            color: "#f8fafc",
            background: "linear-gradient(135deg, #ef4444, #f97316)",
            cursor: "pointer",
          }}
        >
          Log out
        </button>
      </section>
    </main>
  );
};

export default DashboardPage;
