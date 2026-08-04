import CreateChannel from "./CreateChannel.jsx";
import {useEffect, useState} from "react";
import {FaPlus} from "react-icons/fa";
import {useInfiniteQuery} from "@tanstack/react-query";
import {CHANNELS_QUERY_KEY, getSubscribedChannelsApi} from "../api/channelApi.js";
import ResizeBar from "../components/ResizeBar.jsx";
import Channel from "../components/Channel.jsx";
import {Link} from "react-router-dom";


function ChannelAside() {
    const [isMobile, setIsMobile] = useState(window.innerWidth < 768);
    const [showCreateChannelForm, setShowCreateChannelForm] = useState(false);

    const [width, setWidth] = useState(() => {
        const saved = localStorage.getItem("channels-sidebar-width");
        return saved ? Number(saved) : 320;
    });

    useEffect(() => {
        localStorage.setItem("channels-sidebar-width", "" + width);
    }, [width]);

    useEffect(() => {
        const handleResize = () => {
            setIsMobile(window.innerWidth < 768);
        };

        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    const {
        data,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage,
        isLoading,
        isError,
        error,
        refetch,
    } = useInfiniteQuery({
        queryKey: CHANNELS_QUERY_KEY,
        queryFn: ({pageParam}) => getSubscribedChannelsApi({cursor: pageParam, limit: 20}),
        initialPageParam: null,
        getNextPageParam: (lastPage) => lastPage.hasNext ? lastPage.nextCursor : undefined,
    });

    const channels = data?.pages.flatMap((page) => page.channels ?? []) ?? [];
    const errorMessage = error?.response?.data?.message || error?.message || "Failed to load channels";

    const handleScroll = (e) => {
        const el = e.currentTarget;
        if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
            if (hasNextPage && !isFetchingNextPage) {
                fetchNextPage();
            }
        }
    };

    return <>
        <div className="relative h-full bg-gray-50 shadow-sm"
             style={{
                 width: isMobile ? "100%" : `${width}px`
             }}
        >
            <div className="h-full overflow-y-auto pb-20" onScroll={handleScroll}>
                {isLoading && (
                    <div className="space-y-2 p-2">
                        {Array.from({length: 6}).map((_, index) => (
                            <div
                                key={index}
                                className="flex items-center gap-4 p-2 pl-4 animate-pulse"
                            >
                                <div className="h-12 w-12 shrink-0 rounded-full bg-gray-200"/>
                                <div className="flex-1 space-y-2">
                                    <div className="h-4 w-2/3 rounded bg-gray-200"/>
                                    <div className="h-3 w-1/3 rounded bg-gray-200"/>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {!isLoading && isError && (
                    <div className="flex flex-col items-center gap-3 p-6 text-center">
                        <p className="text-sm text-red-500">{errorMessage}</p>
                        <button
                            onClick={() => refetch()}
                            className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
                        >
                            Retry
                        </button>
                    </div>
                )}

                {!isLoading && !isError && channels.length === 0 && (
                    <div className="p-6 text-center">
                        <p className="text-sm text-gray-500">
                            No channels yet.
                        </p>
                        <p className="mt-1 text-xs text-gray-400">
                            Tap the + button to create your first channel.
                        </p>
                    </div>
                )}

                {channels.map((channel, index) => (
                    <Link key={channel.channelId || index} to={channel.channelId || index}>
                        <Channel channel={channel} index={index}/>
                    </Link>
                ))}

                {isFetchingNextPage && (
                    <div className="space-y-2 p-2">
                        {Array.from({length: 3}).map((_, index) => (
                            <div
                                key={index}
                                className="flex items-center gap-4 p-2 pl-4 animate-pulse"
                            >
                                <div className="h-12 w-12 shrink-0 rounded-full bg-gray-200"/>
                                <div className="flex-1 space-y-2">
                                    <div className="h-4 w-2/3 rounded bg-gray-200"/>
                                    <div className="h-3 w-1/3 rounded bg-gray-200"/>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            <ResizeBar width={width} setWidth={setWidth}/>

            <button
                onClick={() => setShowCreateChannelForm(prev => !prev)}
                className="fixed md:absolute bottom-25 md:bottom-40 right-6
                                 h-8 w-8 rounded-full
                               bg-blue-600 text-white
                               shadow-lg
                               flex items-center justify-center
                               hover:bg-blue-700
                               active:scale-95
                               transition"
            >
                <FaPlus size={20}/>
            </button>
        </div>

        {showCreateChannelForm && (
            <CreateChannel setShowCreateChannelForm={setShowCreateChannelForm}/>
        )}
    </>
}

export default ChannelAside;