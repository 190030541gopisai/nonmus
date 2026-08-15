import {useEffect, useMemo, useRef, useState} from "react";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import {FaCheckCircle, FaFolderOpen, FaLink, FaSignOutAlt, FaUsers, FaVideo} from "react-icons/fa";
import {IoArrowBackCircleOutline} from "react-icons/io5";
import {BsThreeDotsVertical} from "react-icons/bs";
import {CHANNELS_QUERY_KEY, getChannelByIdApi, getSubscribedChannelsApi} from "../api/channelApi.js";
import ChannelLogo from "./ChannelLogo.jsx";
import LoadingFallback from "../../../routes/LoadingFallback.jsx";

function formatCompact(count) {
    if (count == null) return "0";
    if (count >= 1_000_000)
        return `${(count / 1_000_000).toFixed(count % 1_000_000 === 0 ? 0 : 1).replace(/\.0$/, "")}M`;
    if (count >= 1_000)
        return `${(count / 1_000).toFixed(count % 1_000 === 0 ? 0 : 1).replace(/\.0$/, "")}K`;
    return String(count);
}

const TABS = [
    {key: "videos", label: "Videos", icon: FaVideo},
    {key: "FoldersAndFiles", label: "Files", icon: FaFolderOpen},
];

function EmptyTab({icon: Icon, title, description}) {
    return (
        <div className="flex flex-col items-center justify-center gap-3 py-20 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-slate-100">
                <Icon className="h-7 w-7 text-slate-400"/>
            </div>
            <h3 className="text-base font-semibold text-slate-900">{title}</h3>
            <p className="max-w-sm text-sm text-slate-500">{description}</p>
        </div>
    );
}

function ChannelContent() {
    const {id} = useParams();
    const navigate = useNavigate();
    const {pathname} = useLocation();

    const descriptionRef = useRef(null);
    const [showDescription, setShowDescription] = useState(false);
    const [isClamped, setIsClamped] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);

    const {data: channel, isLoading, isError, error, refetch} = useQuery({
        queryKey: ["channel", id],
        queryFn: () => getChannelByIdApi(id),
        enabled: !!id,
    });

    // Active tab derived from the URL so deep links / refresh keep state.
    const activeTab = useMemo(() => {
        const found = TABS.find((t) => pathname.includes(`/${t.key}`));
        return found?.key ?? TABS[0].key;
    }, [pathname]);

    const subscribersLabel = channel?.subscribersCount === 1 ? "subscriber" : "subscribers";

    useEffect(() => {
        const el = descriptionRef.current;
        if (!el) return;
        setIsClamped(el.scrollHeight > el.clientHeight + 1);
    }, [channel?.description, showDescription]);

    if (!id) {
        return (
            <div className="flex h-full items-center justify-center p-6">
                <div className="text-center">
                    <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-blue-100">
                        <FaUsers className="text-xl text-blue-600"/>
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
        return <LoadingFallback />;
    }

    if (isError) {
        const errorResponse = error?.response?.data;
        const errorCode = errorResponse.errorCode;
        const errorMessage = errorResponse?.message

        if(errorCode === 'CHANNEL_NOT_FOUND') {
            return <div className="flex flex-col items-center gap-3 p-6 text-center">
                <p className="text-sm text-red-500">{errorMessage}</p>

                <button
                    onClick={() => navigate("/channels")}
                    aria-label="Go back"
                    className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
                >
                    Go back
                </button>
            </div>
        }

        return (
            <div className="flex flex-col items-center gap-3 p-6 text-center">
                <p className="text-sm text-red-500">{errorMessage}</p>
                <button
                    onClick={() => refetch()}
                    className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
                >
                    Please try again
                </button>
            </div>
        );
    }

    return (
        <div className="flex h-full flex-col bg-white">
            {/* Channel identity card */}
            <section className="relative m-4">
                <button
                    onClick={() => navigate("/channels")}
                    aria-label="Go back"
                    className="absolute top-2 left-1 z-30 rounded-full p-1.5 px-0 transition hover:bg-white/20 md:hidden"
                >
                    <IoArrowBackCircleOutline className="h-9 w-9"/>
                </button>

                {channel.member && (
                    <button
                        onClick={() => setMenuOpen((v) => !v)}
                        aria-label="More options"
                        className="absolute top-2 right-2 z-30 rounded-full p-2 transition hover:bg-white/20"
                    >
                        <BsThreeDotsVertical className="h-5 w-5"/>
                    </button>
                )}

                {menuOpen && (
                    <>
                        <div
                            className="fixed inset-0 z-20"
                            onClick={() => setMenuOpen(false)}
                        />
                        <div
                            className="absolute right-6 top-12 z-30 w-56 overflow-hidden rounded-xl bg-white shadow-lg">
                            {channel?.owner && (
                                <button
                                    onClick={() => {
                                        setMenuOpen(false);
                                        navigate(`/channels/${id}/invites`);
                                    }}
                                    className="flex w-full items-center gap-3 px-4 py-3 text-left text-sm font-medium text-slate-700 transition hover:bg-slate-50"
                                >
                                    <FaLink className="h-4 w-4 text-slate-400"/>
                                    Manage Invites
                                </button>
                            )}

                            {channel.member && (
                                <button
                                    className="flex w-full items-center gap-3 px-4 py-3 text-left text-sm font-medium text-slate-700 transition hover:bg-slate-50"
                                >
                                    <FaSignOutAlt /> Leave Channel
                                </button>
                            )}
                        </div>
                    </>
                )}

                <div className="flex flex-col items-center gap-5 md:flex-row md:items-end">
                    <div className="shrink-0 rounded-full ring-4 ring-white">
                        <ChannelLogo channel={channel} size="lg"/>
                    </div>

                    <div className="flex-1 text-center md:pb-2 md:text-left">
                        <div className="flex items-center justify-center gap-2 md:justify-start">
                            <h1 className="text-2xl font-bold tracking-tighhover:-translate-1t text-slate-900 md:text-3xl">
                                {channel?.name}
                            </h1>
                            {channel?.isVerified && <FaCheckCircle className="text-sky-500"/>}
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
                            style={{overflowWrap: "anywhere", wordBreak: "break-word"}}
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
                    {TABS.map(({key, label, icon: Icon}) => {
                        const isActive = activeTab === key;
                        return (
                            <button
                                key={key}
                                onClick={() => navigate(`/channels/${id}/${key}`)}
                                className={`relative flex items-center gap-2 whitespace-nowrap px-4 py-3 text-sm font-medium transition ${
                                    isActive ? "text-slate-900" : "text-slate-500 hover:text-slate-800"
                                }`}
                            >
                                <Icon className="h-4 w-4"/>
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
                {activeTab === "videos" && (
                    <EmptyTab
                        icon={FaVideo}
                        title="No videos yet"
                        description="Videos posted in this channel will appear here."
                    />
                )}
                {activeTab === "FoldersAndFiles" && (
                    <EmptyTab
                        icon={FaFolderOpen}
                        title="No files yet"
                        description="Files and folders shared in this channel will appear here."
                    />
                )}
            </div>
        </div>
    );
}

export default ChannelContent;