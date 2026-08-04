import {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import {FaUsers, FaVideo} from "react-icons/fa";
import {getChannelByIdApi} from "../api/channelApi.js";
import ChannelLogo from "./ChannelLogo.jsx";

const TABS = [
    {key: "videos", label: "Videos", icon: FaVideo},
    {key: "members", label: "Members", icon: FaUsers},
];

function ChannelContent() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState("videos");

    const {
        data: channel,
        isLoading,
        isError,
        error,
        refetch,
    } = useQuery({
        queryKey: ["channel", id],
        queryFn: () => getChannelByIdApi(id),
        enabled: !!id,
    });

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

    const errorMessage = error?.response?.data?.message || error?.message || "Failed to load channel";

    if (isLoading) {
        return (
            <div className="animate-pulse p-6">
                <div className="flex items-center gap-6">
                    <div className="h-20 w-20 shrink-0 rounded-full bg-gray-200"/>
                    <div className="flex-1 space-y-3">
                        <div className="h-6 w-1/3 rounded bg-gray-200"/>
                        <div className="h-4 w-1/4 rounded bg-gray-200"/>
                        <div className="h-4 w-1/5 rounded bg-gray-200"/>
                    </div>
                </div>
                <div className="mt-6 space-y-3">
                    <div className="h-3 w-2/3 rounded bg-gray-200"/>
                    <div className="h-3 w-1/2 rounded bg-gray-200"/>
                </div>
            </div>
        );
    }

    if (isError) {
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

    const memberLabel = channel.subscribersCount === 1 ? "member" : "members";

    return (
        <div className="h-full overflow-y-auto">
            <div className="bg-gradient-to-r from-blue-600 to-indigo-600 px-6 py-8">
                <button
                    type="button"
                    onClick={() => navigate("/channels")}
                    className="mb-4 flex items-center gap-2 text-sm font-medium text-blue-100 transition hover:text-white md:hidden"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <polyline points="15 18 9 12 15 6" />
                    </svg>
                    Back to channels
                </button>
                <div className="flex items-center gap-6">
                    <div className="shrink-0 rounded-full bg-white p-1">
                        <ChannelLogo channel={channel} size="xl"/>
                    </div>
                    <div className="min-w-0">
                        <h1 className="truncate text-2xl font-bold text-white">
                            {channel.name}
                        </h1>
                        {channel.handle && (
                            <p className="truncate text-sm text-blue-100">
                                @{channel.handle}
                            </p>
                        )}
                        <div className="mt-1 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-blue-100">
                            <span>{channel.subscribersCount} {memberLabel}</span>
                            <span>
                                Joined {new Date(channel.createdAt).toLocaleDateString(undefined, {
                                    month: "long",
                                    year: "numeric",
                                })}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="border-b border-slate-200 bg-white">
                <div className="flex">
                    {TABS.map(({key, label, icon: Icon}) => (
                        <button
                            key={key}
                            onClick={() => setActiveTab(key)}
                            className={`flex items-center gap-2 px-5 py-3 text-sm font-medium transition ${
                                activeTab === key
                                    ? "border-b-2 border-blue-600 text-blue-600"
                                    : "text-slate-500 hover:text-slate-700"
                            }`}
                        >
                            <Icon size={15}/>
                            {label}
                        </button>
                    ))}
                </div>
            </div>

            <div className="p-6">
                {channel.description && (
                    <p className="mb-6 text-sm leading-relaxed text-slate-600">
                        {channel.description}
                    </p>
                )}

                {activeTab === "videos" && (
                    <div className="rounded-xl border border-dashed border-slate-300 p-10 text-center">
                        <FaVideo className="mx-auto mb-3 text-2xl text-slate-300"/>
                        <p className="text-sm text-slate-500">No videos yet.</p>
                    </div>
                )}

                {activeTab === "members" && (
                    <div className="rounded-xl border border-dashed border-slate-300 p-10 text-center">
                        <FaUsers className="mx-auto mb-3 text-2xl text-slate-300"/>
                        <p className="text-sm text-slate-500">No members yet.</p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ChannelContent;
