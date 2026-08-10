import {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {Link} from "react-router-dom";
import {FaPlus} from "react-icons/fa";
import {List} from "react-window";
import {useInfiniteQuery} from "@tanstack/react-query";
import {CHANNELS_QUERY_KEY, getSubscribedChannelsApi} from "../api/channelApi.js";
import CreateChannel from "./CreateChannel.jsx";
import Channel from "./Channel.jsx";
import ResizeBar from "./ResizeBar.jsx";

const INITIAL_ROW_HEIGHT = 64;
const OVERSCAN = 8;
const RESERVE_ROWS = 8;
const PAGE_SIZE = 20;
const ROW_CLASS = "flex px-4 py-2 shadow-sm hover:bg-gray-100 hover:-translate-1 transition transform duration-300 gap-3";

let asideScrollRestore = 0;

function ChannelRow({index, style, channels, className, isFetchingNextPage}) {
    const channel = channels[index];

    if (!channel) {
        if (isFetchingNextPage) {
            return (
                <div
                    style={style}
                    className="flex items-center gap-3 px-4 py-2 animate-pulse"
                >
                    <div className="h-12 w-12 shrink-0 rounded-full bg-gray-200" />

                    <div className="flex-1 space-y-2">
                        <div className="h-4 w-2/3 rounded bg-gray-200" />
                        <div className="h-3 w-1/3 rounded bg-gray-200" />
                    </div>
                </div>
            );
        }

        return null;
    }

    return (
        <Link
            to={channel.channelId || index}
            style={style}
            className="block"
        >
            <Channel
                channel={channel}
                index={index}
                className={className}
            />
        </Link>
    );
}

function ChannelAside({isMobile, width, setWidth}) {
    const [showCreateChannelForm, setShowCreateChannelForm] = useState(false);

    const listRef = useRef(null);
    const rowHeightRef = useRef(INITIAL_ROW_HEIGHT);
    const restoreAppliedRef = useRef(false);

    const [rowHeight, setRowHeight] = useState(INITIAL_ROW_HEIGHT);

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
        queryFn: ({pageParam}) => getSubscribedChannelsApi({cursor: pageParam, limit: PAGE_SIZE}),
        initialPageParam: null,
        getNextPageParam: (lastPage) => lastPage.hasNext ? lastPage.nextCursor : undefined
    });

    const channels = useMemo(
        () => data?.pages.flatMap((page) => page.channels ?? []) ?? [],
        [data],
    );

    const errorMessage = error?.response?.data?.message || error?.message || "Failed to load channels";

    const totalItems = channels.length + (hasNextPage ? RESERVE_ROWS : 0);

    const rowProps = useMemo(
        () => ({channels, className: ROW_CLASS, isFetchingNextPage}),
        [channels, isFetchingNextPage]);

    const measureNode = useCallback((node) => {
        if (!node) return;
        const height = node.offsetHeight;
        if (height > 0 && height !== rowHeightRef.current) {
            rowHeightRef.current = height;
            setRowHeight(height);
        }
    }, []);

    useEffect(() => {
        if (channels.length > 0 && !restoreAppliedRef.current) {
            restoreAppliedRef.current = true;
            if (asideScrollRestore > 0) {
                const index = Math.min(asideScrollRestore, totalItems - 1);
                listRef.current?.scrollToRow({index, behavior: "instant"});
            }
        }
    }, [channels.length, totalItems]);

    const handleScroll = useCallback((e) => {
        asideScrollRestore = Math.max(0, Math.round(e.currentTarget.scrollTop / rowHeight));
    }, [rowHeight]);

    const handleRowsRendered = useCallback((visibleRows) => {
        if (visibleRows && hasNextPage && !isFetchingNextPage && visibleRows.stopIndex >= totalItems - RESERVE_ROWS) {
            fetchNextPage();
        }
    }, [hasNextPage, isFetchingNextPage, totalItems, fetchNextPage]);

    return (
        <>
            <div className="relative flex h-full min-h-0 flex-col">
                {!isLoading && isError && (
                    <div className="flex flex-1 items-center justify-center p-6">
                        <div className="flex max-w-sm flex-col items-center text-center">
                            {/* Error Icon */}
                            <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-red-50">
                                <svg
                                    className="h-7 w-7 text-red-500"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={1.8}
                                        d="M12 9v3.75m0 3.25h.01M10.29 3.86l-7.5 13A2 2 0 004.52 20h14.96a2 2 0 001.73-3.14l-7.5-13a2 2 0 00-3.42 0z"
                                    />
                                </svg>
                            </div>

                            {/* Message */}
                            <h3 className="text-base font-semibold text-gray-900">
                                Something went wrong
                            </h3>

                            <p className="mt-1 max-w-xs text-sm leading-5 text-gray-500">
                                {errorMessage || "We couldn't load this content. Please try again."}
                            </p>

                            {/* Retry */}
                            <button
                                onClick={() => refetch()}
                                className="mt-5 inline-flex h-10 items-center justify-center gap-2 rounded-lg bg-blue-600 px-5 text-sm font-medium text-white shadow-sm transition-all hover:bg-blue-700 hover:shadow-md active:scale-95 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                            >
                                <svg
                                    className="h-4 w-4"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M4 4v5h5M20 20v-5h-5M5.05 9A7 7 0 0117.9 6.1L20 4m-2.95 11A7 7 0 016.1 17.9L4 20"
                                    />
                                </svg>
                                Retry
                            </button>
                        </div>
                    </div>
                )}

                {!isLoading && !isError && channels.length === 0 && (
                    <div className="flex flex-1 items-center justify-center px-6">
                        <div className="flex max-w-xs flex-col items-center text-center">
                            {/* Icon */}
                            <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-50">
                                <span className="text-2xl">📢</span>
                            </div>

                            {/* Title */}
                            <h3 className="text-base font-semibold text-gray-900">
                                No channels yet
                            </h3>

                            {/* Hint */}
                            <p className="mt-3 text-xs text-gray-400">
                                Tap the <span className="font-semibold text-blue-500">+</span> button to get started.
                            </p>
                        </div>
                    </div>
                )}

                {!isLoading && !isError && channels.length > 0 && (
                    <List
                        className="flex-1 min-h-0 overflow-y-auto"
                        listRef={listRef}
                        rowComponent={ChannelRow}
                        rowCount={totalItems}
                        rowHeight={rowHeight}
                        rowProps={rowProps}
                        rowKey={(index, props) => String(props.channels[index]?.channelId ?? index)}
                        overscanCount={OVERSCAN}
                        onScroll={handleScroll}
                        onRowsRendered={handleRowsRendered}
                    />
                )}

                {/* background size calculation not for viewing */}
                {!isLoading && !isError && channels.length > 0 && (
                    <div
                        ref={measureNode}
                        aria-hidden="true"
                        className="pointer-events-none absolute -z-10 left-0 top-0 invisible w-72"
                    >
                        <Channel
                            channel={channels[0]}
                            index={0}
                            className={ROW_CLASS}
                        />
                    </div>
                )}

                {!isMobile && (
                    <ResizeBar
                        width={width}
                        setWidth={setWidth}
                        className="absolute inset-y-0 right-0 z-20 w-1 cursor-ew-resize bg-transparent transition-colors hover:bg-blue-300 active:bg-blue-500"
                    />
                )}

                <button
                    onClick={() => setShowCreateChannelForm(prev => !prev)}
                    className="absolute bottom-5 right-5 bg-blue-500 text-white rounded-full h-10 w-10 flex justify-center items-center"
                >
                    <FaPlus size={20}/>
                </button>
            </div>

            {showCreateChannelForm && (
                <CreateChannel setShowCreateChannelForm={setShowCreateChannelForm}/>
            )}
        </>
    );
}

export default ChannelAside;