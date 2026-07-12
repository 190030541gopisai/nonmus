import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import ProfilePictureUploader from "../components/ProfilePictureUploader";
import {logoutApi} from "../api/authApi.js";

const DashboardPage = () => {
  const { user, logout } = useContext(AuthContext);
  const { name, email } = user || {};
  const navigate = useNavigate();

  const handleLogout = async () => {
        await logout();
        navigate("/login", { replace: true });
  };

  // Derive initials for the greeting avatar fallback
  const initials = name
    ? name
        .split(" ")
        .map((w) => w[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "?";

  return (
    <main
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "24px",
        background:
          "linear-gradient(135deg, #020617 0%, #0f172a 45%, #111827 100%)",
        color: "#e5e7eb",
        fontFamily: "'Inter', system-ui, sans-serif",
      }}
    >
      <section
        style={{
          width: "100%",
          maxWidth: "480px",
          borderRadius: "28px",
          background: "rgba(15, 23, 42, 0.84)",
          border: "1px solid rgba(148, 163, 184, 0.12)",
          boxShadow:
            "0 32px 96px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.04) inset",
          backdropFilter: "blur(20px)",
          overflow: "hidden",
        }}
      >
        {/* Top accent bar */}
        <div
          style={{
            height: "4px",
            background:
              "linear-gradient(90deg, #7c3aed, #a855f7, #ec4899, #f97316)",
          }}
        />

        <div style={{ padding: "40px 36px 36px" }}>
          {/* Profile picture + name header */}
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              gap: "16px",
              marginBottom: "36px",
            }}
          >
            <ProfilePictureUploader />

            <div style={{ textAlign: "center" }}>
              <h1
                style={{
                  margin: 0,
                  fontSize: "1.6rem",
                  fontWeight: 700,
                  letterSpacing: "-0.02em",
                  color: "#f1f5f9",
                }}
              >
                {name || "Your Profile"}
              </h1>
              <p
                style={{
                  margin: "4px 0 0",
                  fontSize: "0.875rem",
                  color: "#64748b",
                }}
              >
                {email}
              </p>
            </div>
          </div>

          {/* Divider */}
          <div
            style={{
              height: "1px",
              background:
                "linear-gradient(90deg, transparent, rgba(148,163,184,0.15), transparent)",
              marginBottom: "28px",
            }}
          />

          {/* Status row */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "10px",
              padding: "14px 16px",
              borderRadius: "14px",
              background: "rgba(16, 185, 129, 0.08)",
              border: "1px solid rgba(16, 185, 129, 0.2)",
              marginBottom: "24px",
            }}
          >
            <span
              style={{
                width: "8px",
                height: "8px",
                borderRadius: "50%",
                background: "#10b981",
                flexShrink: 0,
                boxShadow: "0 0 6px #10b981",
              }}
            />
            <p
              style={{
                margin: 0,
                fontSize: "0.875rem",
                color: "#a7f3d0",
                fontWeight: 500,
              }}
            >
              Session active — you are logged in
            </p>
          </div>

          {/* Info cards */}
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "12px",
              marginBottom: "28px",
            }}
          >
            {[
              { label: "Name", value: name || "—" },
              { label: "Email", value: email || "—" },
            ].map(({ label, value }) => (
              <div
                key={label}
                style={{
                  padding: "14px 16px",
                  borderRadius: "14px",
                  background: "rgba(255,255,255,0.04)",
                  border: "1px solid rgba(255,255,255,0.07)",
                }}
              >
                <p
                  style={{
                    margin: "0 0 4px",
                    fontSize: "0.7rem",
                    color: "#475569",
                    textTransform: "uppercase",
                    letterSpacing: "0.08em",
                    fontWeight: 600,
                  }}
                >
                  {label}
                </p>
                <p
                  style={{
                    margin: 0,
                    fontSize: "0.9rem",
                    color: "#cbd5e1",
                    fontWeight: 500,
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    whiteSpace: "nowrap",
                  }}
                  title={value}
                >
                  {value}
                </p>
              </div>
            ))}
          </div>

          {/* Log out button */}
          <button
            id="logout-btn"
            type="button"
            onClick={handleLogout}
            style={{
              width: "100%",
              padding: "14px",
              borderRadius: "14px",
              border: "none",
              background: "linear-gradient(135deg, #ef4444, #f97316)",
              color: "#fff",
              fontSize: "0.95rem",
              fontWeight: 700,
              cursor: "pointer",
              letterSpacing: "0.02em",
              transition: "opacity 0.2s, transform 0.15s",
              boxShadow: "0 4px 20px rgba(239,68,68,0.3)",
            }}
            onMouseEnter={(e) => (e.currentTarget.style.opacity = "0.88")}
            onMouseLeave={(e) => (e.currentTarget.style.opacity = "1")}
          >
            Log out
          </button>
        </div>
      </section>
    </main>
  );
};

export default DashboardPage;
