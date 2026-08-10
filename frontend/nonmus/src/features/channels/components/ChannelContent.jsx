import { useEffect, useMemo, useRef, useState } from "react";
import { Outlet, useLocation, useNavigate, useParams } from "react-router-dom";
import { useInfiniteQuery, useQuery } from "@tanstack/react-query";
import { FaUsers, FaVideo, FaCheckCircle, FaFilm, FaFolderOpen, FaBell, FaLink } from "react-icons/fa";
import { IoArrowBackCircleOutline } from "react-icons/io5";
import { BsThreeDotsVertical } from "react-icons/bs";
import { CHANNELS_QUERY_KEY, getChannelByIdApi, getSubscribedChannelsApi } from "../api/channelApi.js";
import ChannelLogo from "./ChannelLogo.jsx";

function formatCompact(count) {
    if (count == null) return "0";
    if (count >= 1_000_000)
        return `${(count / 1_000_000).toFixed(count % 1_000_000 === 0 ? 0 : 1).replace(/\.0$/, "")}M`;
    if (count >= 1_000)
        return `${(count / 1_000).toFixed(count % 1_000 === 0 ? 0 : 1).replace(/\.0$/, "")}K`;
    return String(count);
}

const TABS = [
    { key: "videos", label: "Videos", icon: FaVideo },
    { key: "FoldersAndFiles", label: "Files", icon: FaFolderOpen },
];

function ChannelContent() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { pathname } = useLocation();

    const descriptionRef = useRef(null);
    const [showDescription, setShowDescription] = useState(false);
    const [isClamped, setIsClamped] = useState(false);
    const [logoError, setLogoError] = useState(false);
    const [subscribed, setSubscribed] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);

    const { data: channel, isLoading, isError, error, refetch } = useQuery({
        queryKey: ["channel", id],
        queryFn: () => getChannelByIdApi(id),
        enabled: !!id,
    });

    const subscribedData = useInfiniteQuery({
        queryKey: CHANNELS_QUERY_KEY,
        queryFn: ({ pageParam }) => getSubscribedChannelsApi({ cursor: pageParam, limit: 20 }),
        initialPageParam: null,
        getNextPageParam: (lastPage) => lastPage.hasNext ? lastPage.nextCursor : undefined,
    });

    const subscribedChannelCount = subscribedData.data?.pages.flatMap((page) => page.channels ?? []).length ?? 0;

    // Active tab derived from the URL so deep links / refresh keep state.
    const activeTab = useMemo(() => {
        const found = TABS.find((t) => pathname.includes(`/${t.key}`));
        return found?.key ?? TABS[0].key;
    }, [pathname]);

    const goToTab = (key) => navigate(`/channels/${id}/${key}`);

    const subscribersLabel = channel?.subscribersCount === 1 ? "subscriber" : "subscribers";

    useEffect(() => {
        const el = descriptionRef.current;
        if (!el) return;
        setIsClamped(el.scrollHeight > el.clientHeight + 1);
    }, [channel?.description, showDescription]);

    if (!id) {
        if (subscribedChannelCount === 0) {
            return <div className="hidden md:block flex-1" />;
        }
        return (
            <div className="hidden md:flex h-full items-center justify-center p-6">
                <div className="text-center">
                    <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-blue-100">
                        <FaUsers className="text-xl text-blue-600" />
                    </div>
                    <h2 className="text-lg font-semibold text-slate-900">No channel selected</h2>
                    <p className="mt-1 text-sm text-slate-500">
                        Select a channel from the sidebar to view its details.
                    </p>
                </div>
            </div>
        );
    }

    if (isLoading) {
        return (
            <div className="animate-pulse">
                <div className="h-28 w-full bg-gray-200 md:h-40" />
                <div className="p-6">
                    <div className="flex flex-col items-center gap-6 md:flex-row md:items-start">
                        <div className="-mt-16 h-[120px] w-[120px] shrink-0 rounded-full border-4 border-white bg-gray-200 md:h-[160px] md:w-[160px]" />
                        <div className="w-full flex-1 space-y-3 text-center md:text-left">
                            <div className="mx-auto h-8 w-1/2 rounded bg-gray-200 md:mx-0 md:h-10 md:w-1/3" />
                            <div className="mx-auto h-4 w-1/3 rounded bg-gray-200 md:mx-0" />
                            <div className="mx-auto h-4 w-2/3 rounded bg-gray-200 md:mx-0" />
                            <div className="mx-auto h-10 w-32 rounded-full bg-gray-200 md:mx-0" />
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (isError) {
        const errorMessage =
            error?.response?.data?.message || error?.message || "Failed to load channel";
        return (
            <div className="flex flex-col items-center gap-3 p-6 text-center">
                <p className="text-sm text-red-500">{errorMessage}</p>
                <button
                    onClick={() => refetch()}
                    className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="relative flex h-full flex-col overflow-y-auto bg-white">
            <button
                onClick={() => navigate("/channels")}
                aria-label="Go back"
                className="md:hidden absolute top-2 left-2 rounded-full text-white transition"
            >
                <IoArrowBackCircleOutline className="h-9 w-9" />
            </button>

            <button
                onClick={() => setMenuOpen((v) => !v)}
                aria-label="More options"
                className="absolute top-2 right-2 rounded-full p-2  transition text-white "
            >
                <BsThreeDotsVertical className="h-5 w-5" />
            </button>

            {menuOpen && (
                <>
                    <div className="fixed inset-0 z-10" onClick={() => setMenuOpen(false)} />
                    <div className="absolute top-10 right-5 z-20 w-56 overflow-hidden rounded-lg border border-slate-200 bg-white py-1 shadow-lg">
                        <button
                            onClick={() => {
                                setMenuOpen(false);
                                navigate(`/channels/${id}/invites`);
                            }}
                            className="flex w-full items-center gap-2 px-4 py-2.5 text-sm text-slate-700 transition hover:bg-slate-50"
                        >
                            <FaLink className="h-4 w-4" />
                            Manage Invites
                        </button>
                    </div>
                </>
            )}

            <div className="h-28 w-full bg-gradient-to-r from-sky-500 via-indigo-500 to-fuchsia-500 md:p-16">
                {channel?.bannerUrl && (
                    <img
                        src={channel.bannerUrl}
                        alt=""
                        className="h-full w-full object-cover"
                        onError={(e) => (e.currentTarget.style.display = "none")}
                    />
                )}
            </div>

            {/* Channel identity card */}
            <section className="px-4 md:px-8">
                <div className="flex flex-col items-center gap-5 md:flex-row md:items-end">
                    <div className="-mt-12 shrink-0 rounded-full ring-4 ring-white md:-mt-16">
                        <ChannelLogo channel={channel} size="lg" onError={() => setLogoError(true)} />
                    </div>

                    <div className="flex-1 text-center md:pb-2 md:text-left">
                        <div className="flex items-center justify-center gap-2 md:justify-start">
                            <h1 className="text-2xl font-bold tracking-tight text-slate-900 md:text-3xl">
                                {channel?.name}
                            </h1>
                            {channel?.isVerified && <FaCheckCircle className="text-sky-500" />}
                        </div>
                        <p className="mt-1 text-sm text-slate-500">
                            {channel?.handle ? `@${channel.handle} · ` : ""}
                            <span className="font-medium text-slate-700">
                                {formatCompact(channel?.subscribersCount)}
                            </span>{" "}
                            {subscribersLabel}
                        </p>
                    </div>
                </div>

                {/* Description with more / less */}
                {channel?.description && (
                    <div className="mt-4">
                        <div
                            ref={descriptionRef}
                            className={`whitespace-pre-line text-sm leading-relaxed text-slate-600 ${
                                showDescription ? "" : "line-clamp-2"
                            }`}
                            style={{ overflowWrap: "anywhere", wordBreak: "break-word" }}
                        >
                            {channel.description}
                        </div>
                        {(isClamped || showDescription) && (
                            <button
                                onClick={() => setShowDescription((v) => !v)}
                                className="mt-1 text-sm font-semibold text-slate-900 hover:underline"
                            >
                                {showDescription ? "less" : "...more"}
                            </button>
                        )}
                    </div>
                )}
            </section>

            {/* Sticky tab bar */}
            <nav className="sticky top-0 z-10 mt-4 border-b border-slate-200 bg-white/90 px-4 backdrop-blur md:px-8">
                <div className="flex gap-1 overflow-x-auto">
                    {TABS.map(({ key, label, icon: Icon }) => {
                        const isActive = activeTab === key;
                        return (
                            <button
                                key={key}
                                onClick={() => goToTab(key)}
                                className={`relative flex items-center gap-2 whitespace-nowrap px-4 py-3 text-sm font-medium transition ${
                                    isActive ? "text-slate-900" : "text-slate-500 hover:text-slate-800"
                                }`}
                            >
                                <Icon className="h-4 w-4" />
                                {label}
                                <span
                                    className={`absolute inset-x-2 -bottom-px h-[3px] rounded-full transition ${
                                        isActive ? "bg-slate-900" : "bg-transparent"
                                    }`}
                                />
                            </button>
                        );
                    })}
                </div>
            </nav>

            {/* Tab content */}
            <div className="flex-1 px-4 py-5 md:px-8">

                    COntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsd

                COntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsd

                COntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsd

                COntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsd

                COntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsd

                COntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsdCOntent ssdfsd

            </div>
        </div>
    );
}

export default ChannelContent;