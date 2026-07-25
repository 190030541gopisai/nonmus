import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import ProfilePictureUploader from "../components/ProfilePictureUploader";

const DashboardPage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { name, email } = user || {};

  const handleLogout = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-[#020617] via-[#0f172a] to-[#111827] p-6 text-[#e5e7eb] font-sans">
      <section className="w-full max-w-[480px] overflow-hidden rounded-[28px] border border-white/10 bg-[rgba(15,23,42,0.84)] shadow-[0_32px_96px_rgba(0,0,0,0.5),0_0_0_1px_rgba(255,255,255,0.04)_inset] backdrop-blur-xl">
        <div className="h-1 bg-gradient-to-r from-purple-500 via-fuchsia-500 to-pink-500 via-50% to-orange-500" />
        <div className="px-9 pb-9 pt-10">
          <div className="mb-9 flex flex-col items-center gap-4">
            <ProfilePictureUploader />
            <div className="text-center">
              <h1 className="m-0 text-[1.6rem] font-bold tracking-tight text-slate-100">
                {name || "Your Profile"}
              </h1>
              {email && <p className="m-0 mt-1 text-sm text-slate-500">{email}</p>}
            </div>
          </div>

          <div className="mb-7 h-px bg-gradient-to-r from-transparent via-white/10 to-transparent" />

          <div className="mb-6 flex items-center gap-2.5 rounded-[14px] border border-emerald-500/20 bg-emerald-500/10 px-4 py-3.5">
            <span className="h-2 w-2 shrink-0 rounded-full bg-emerald-400 shadow-[0_0_6px_#10b981]" />
            <p className="m-0 text-sm font-medium text-emerald-200">Session active — you are logged in</p>
          </div>

          <div className="mb-7 grid grid-cols-2 gap-3">
            <InfoCard label="Name" value={name || "\u2014"} />
            <InfoCard label="Email" value={email || "\u2014"} />
          </div>

          <button
            id="logout-btn"
            type="button"
            onClick={handleLogout}
            className="w-full cursor-pointer rounded-[14px] border-none bg-gradient-to-r from-red-500 to-orange-500 px-4 py-3.5 text-[0.95rem] font-bold text-white tracking-wide shadow-[0_4px_20px_rgba(239,68,68,0.3)] transition-opacity duration-200 hover:opacity-85"
          >
            Log out
          </button>
        </div>
      </section>
    </main>
  );
};

function InfoCard({ label, value }) {
  return (
    <div className="rounded-[14px] border border-white/10 bg-white/5 px-4 py-3.5">
      <p className="m-0 mb-1 text-[0.7rem] font-semibold uppercase tracking-wider text-slate-600">
        {label}
      </p>
      <p className="m-0 truncate text-[0.9rem] font-medium text-slate-300" title={value}>
        {value}
      </p>
    </div>
  );
}

export default DashboardPage;
