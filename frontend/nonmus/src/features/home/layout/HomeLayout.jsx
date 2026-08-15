import {MdVideocam} from "react-icons/md";
import {GrChannel} from "react-icons/gr";
import {CgProfile} from "react-icons/cg";
import {FaHome, FaSearch} from "react-icons/fa";
import {Link, Outlet, useLocation} from "react-router-dom";
import {IoMdNotifications} from "react-icons/io";
import {TbLayoutSidebarLeftCollapseFilled, TbLayoutSidebarLeftExpandFilled} from "react-icons/tb";
import {useState} from "react";
import EmailVerificationBanner from "../../auth/components/EmailVerificationBanner.jsx";
import {useAuth} from "../../auth/hooks/useAuth.js";


const navItems = [
    {
        to: "/",
        label: "Home",
        icon: <FaHome/>,
        requiresEmailVerification: false,
    },
    {
        to: "/videos",
        label: "Videos",
        icon: <MdVideocam/>,
        requiresEmailVerification: false,
    },
    {
        to: "/channels",
        label: "Channels",
        icon: <GrChannel/>,
        requiresEmailVerification: true,
    },
    {
        to: "/profile",
        label: "Profile",
        icon: <CgProfile/>,
        requiresEmailVerification: false,
    },
];


function HomeLayout() {
    const location = useLocation();
    const {emailVerified} = useAuth();

    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

    return <div className="flex h-screen max-h-screen ">
        <aside
            className={`hidden lg:flex lg:z-10 lg:flex-col border-r border-slate-200 ${sidebarCollapsed ? 'lg:w-16' : 'lg:w-56'}`}>
            <div className="flex h-14 items-center px-3">
                {sidebarCollapsed ? (
                    <button
                        type="button"
                        onClick={() => setSidebarCollapsed(false)}
                        className="mx-auto rounded-lg p-1.5 text-slate-500 hover:bg-slate-100 hover:text-slate-700"
                    >
                        <TbLayoutSidebarLeftExpandFilled size={20}/>
                    </button>
                ) : (
                    <div className="flex w-full items-center justify-between">
                        <div className="flex flex-row justify-center items-center gap-2">
                            <Link to="/">
                                <img src="/logo.png" alt="nonmus" className="h-7 w-7"/>
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
                            <TbLayoutSidebarLeftCollapseFilled size={20}/>
                        </button>
                    </div>
                )}
            </div>
            <nav className="flex-1 space-y-4 px-2 py-4">
                {navItems.map(({to, label, icon, requiresEmailVerification}) => {
                    const isActive = location.pathname === to;
                    const isAllowed = !requiresEmailVerification || emailVerified;

                    if (!isAllowed) {
                        return null;
                    }

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
                            {icon}
                            {!sidebarCollapsed && label}
                        </Link>
                    )
                })
                }
            </nav>

        </aside>

        <div className="flex min-h-screen flex-1 flex-col">
            {/* Header */}
            <header className="flex shrink-0 items-center p-2">
                {/* Logo - mobile/tablet */}
                <div className="flex flex-row items-center justify-center gap-2 lg:hidden">
                    <Link to="/">
                        <img
                            src="/logo.png"
                            alt="nonmus"
                            className="h-7 w-7"
                        />
                    </Link>

                    <Link
                        to="/"
                        className="text-xl font-bold tracking-tight text-slate-900"
                    >
                        Nonmus
                    </Link>
                </div>

                <div className="flex-1"/>

                {/* Right actions */}
                <div className="flex items-center gap-3">
                    <button
                        className="rounded-xl p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-700"
                    >
                        <FaSearch/>
                    </button>

                    <button
                        className="rounded-xl p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-700"
                    >
                        <IoMdNotifications/>
                    </button>
                </div>
            </header>

            <EmailVerificationBanner/>

            {/* Scrollable content */}
            <main className="min-h-0 flex-1 overflow-y-auto">
                <Outlet/>
            </main>

            {/* Mobile bottom navigation */}
            <nav className="shrink-0 border-t border-slate-200 bg-white lg:hidden">
                <div className="mx-auto flex max-w-7xl justify-around">
                    {navItems.map(({to, label, icon, requiresEmailVerification}) => {
                        const isAllowed = !requiresEmailVerification || emailVerified;

                        if (!isAllowed) {
                            return null;
                        }

                        return (
                            <Link
                                key={to}
                                to={to}
                                className="flex flex-col items-center justify-center gap-1 p-3 text-sm text-slate-600"
                            >
                                {icon}
                                <span>{label}</span>
                            </Link>
                        );
                    })
                    }
                </div>
            </nav>
        </div>
    </div>
}

export default HomeLayout;
