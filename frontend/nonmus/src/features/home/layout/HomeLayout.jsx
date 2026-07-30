import { Link, Outlet, useLocation } from "react-router-dom";
import { useState } from "react";
import { TbLayoutSidebarLeftCollapseFilled, TbLayoutSidebarLeftExpandFilled, TbLogout } from "react-icons/tb";
import {useAuth} from "../../auth/hooks/useAuth.js";
import EmailVerificationBanner from "../../auth/components/EmailVerificationBanner.jsx";

function HomeIcon({ active }) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
      <polyline points="9 22 9 12 15 12 15 22" />
    </svg>
  );
}

function VideosIcon({ active }) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polygon points="23 7 16 12 23 17 23 7" />
      <rect x="1" y="5" width="15" height="14" rx="2" ry="2" />
    </svg>
  );
}

function ReelsIcon({ active }) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="2" y="2" width="20" height="20" rx="2.18" ry="2.18" />
      <line x1="7" y1="2" x2="7" y2="22" />
      <line x1="17" y1="2" x2="17" y2="22" />
      <line x1="2" y1="12" x2="22" y2="12" />
      <line x1="2" y1="7" x2="7" y2="7" />
      <line x1="2" y1="17" x2="7" y2="17" />
      <line x1="17" y1="7" x2="22" y2="7" />
      <line x1="17" y1="17" x2="22" y2="17" />
    </svg>
  );
}

function ChannelsIcon({ active }) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  );
}

function ProfileIcon({ active }) {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  );
}

function SearchIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="11" cy="11" r="8" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" />
    </svg>
  );
}

function BellIcon() {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
      <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
    </svg>
  );
}

const navItems = [
  { to: "/", label: "Home", icon: HomeIcon },
  { to: "/videos", label: "Videos", icon: VideosIcon },
  { to: "/reels", label: "Reels", icon: ReelsIcon },
  { to: "/channels", label: "Channels", icon: ChannelsIcon },
  { to: "/profile", label: "Profile", icon: ProfileIcon },
];

function HomeLayout() {
  const location = useLocation();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const { logout } = useAuth();

  return (
    <div className="mx-auto flex min-h-screen  bg-slate-50">
      <aside className={`hidden lg:flex lg:flex-col lg:border-r lg:border-slate-200 lg:bg-white transition-all duration-300 ${sidebarCollapsed ? 'lg:w-16' : 'lg:w-56'}`}>
        <div className="flex h-14 items-center border-b border-slate-200 px-3">
          {sidebarCollapsed ? (
            <button
                type="button"
                onClick={() => setSidebarCollapsed(false)}
                className="mx-auto rounded-lg p-1.5 text-slate-500 hover:bg-slate-100 hover:text-slate-700"
            >
              <TbLayoutSidebarLeftExpandFilled size={20} />
            </button>
          ) : (
            <div className="flex w-full items-center justify-between">
                <div className="flex flex-row justify-center items-center gap-2">
                    <Link to="/">
                        <img src="/logo.png" alt="nonmus" className="h-7 w-7" />
                    </Link>
                    <Link to="/" className="text-xl font-bold tracking-tight text-slate-900">
                        Nonmus
                    </Link>
                </div>
              <button
                  type="button"
                  onClick={() => setSidebarCollapsed(true)}
                  className="rounded-lg p-1.5 text-slate-500 hover:bg-slate-100 hover:text-slate-700"
              >
                <TbLayoutSidebarLeftCollapseFilled size={20} />
              </button>
            </div>
          )}
        </div>
        <nav className="flex-1 space-y-1 px-2 py-4">
          {navItems.map(({ to, label, icon: Icon }) => {
            const isActive = location.pathname === to;
            return (
              <Link
                key={to}
                to={to}
                title={sidebarCollapsed ? label : undefined}
                className={`flex items-center rounded-lg px-3 py-2.5 text-sm font-medium transition ${
                  sidebarCollapsed ? 'justify-center' : 'gap-3'
                } ${
                  isActive
                    ? "bg-slate-100 text-slate-900"
                    : "text-slate-500 hover:bg-slate-50 hover:text-slate-700"
                }`}
              >
                <Icon active={isActive} />
                {!sidebarCollapsed && label}
              </Link>
            );
          })}
        </nav>
        <div className="border-t border-slate-200 px-2 py-3">
          <button
              type="button"
              onClick={logout}
              title={sidebarCollapsed ? "Logout" : undefined}
              className={`flex w-full items-center rounded-lg px-3 py-2.5 text-sm font-medium text-slate-500 transition hover:bg-red-50 hover:text-red-600 ${
                sidebarCollapsed ? 'justify-center' : 'gap-3'
              }`}
          >
            <TbLogout size={20} />
            {!sidebarCollapsed && "Logout"}
          </button>
        </div>
      </aside>

      <div className="flex flex-1 flex-col">
        <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/80 backdrop-blur-lg lg:bg-white">
          <div className="flex items-center justify-between px-4 py-3 sm:px-6">
              <div className="flex flex-row justify-center items-center gap-2 lg:hidden">
                  <Link to="/">
                      <img src="/logo.png" alt="nonmus" className="h-7 w-7" />
                  </Link>
                  <Link to="/" className="text-xl font-bold tracking-tight text-slate-900">
                      Nonmus
                  </Link>
              </div>
              <div />
              <div className="flex items-center gap-3">
                  <button className="rounded-xl p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-700">
                    <SearchIcon />
                  </button>
                  <button className="rounded-xl p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-700">
                    <BellIcon />
                  </button>
              </div>
          </div>
        </header>

        <EmailVerificationBanner />

        <main className="flex-1 pb-20 lg:pb-0">
          <Outlet />
        </main>

        <nav className="fixed bottom-0 left-0 right-0 z-40 border-t border-slate-200 bg-white/80 backdrop-blur-lg lg:hidden">
          <div className="mx-auto flex max-w-7xl items-center justify-around px-2 py-2">
            {navItems.map(({ to, label, icon: Icon }) => {
              const isActive = location.pathname === to;
              return (
                <Link
                  key={to}
                  to={to}
                  className={`flex flex-col items-center gap-0.5 rounded-xl px-3 py-1.5 text-xs font-medium transition ${
                    isActive
                      ? "text-slate-900"
                      : "text-slate-400 hover:text-slate-600"
                  }`}
                >
                  <Icon active={isActive} />
                  {label}
                </Link>
              );
            })}
          </div>
        </nav>
      </div>
    </div>
  );
}

export default HomeLayout;
