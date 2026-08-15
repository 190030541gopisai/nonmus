import {useCallback, useRef, useState} from "react";
import {Link} from "react-router-dom";
import {FaPlus} from "react-icons/fa";
import CreateChannel from "./CreateChannel.jsx";
import Channel from "./Channel.jsx";
import ResizeBar from "./ResizeBar.jsx";
import {useSubscribedChannels} from "../hooks/useChannels.js";
import {List} from "react-window";

function ChannelRow({index, style, channels, isFetchingNextPage}) {
    const channel = channels[index];

    if(!channel) {
        if (isFetchingNextPage) {
            return (
                <div
                    className="flex items-center gap-3 px-4 py-2 animate-pulse shadow-sm"
                    style={style}
                >
                    <div className="h-12 w-12 shrink-0 rounded-full bg-gray-200"/>

                    <div className="flex-1 space-y-2">
                        <div className="h-4 w-2/3 rounded bg-gray-200"/>
                        <div className="h-3 w-1/3 rounded bg-gray-200"/>
                    </div>
                </div>
            );
        }

        return null;
    }

    return (
        <Link
            key={channel.channelId || index}
            to={channel.channelId || index}
            className="block"
            style={style}
        >
            <Channel
                channel={channel}
                index={index}
                className="flex px-4 py-2 shadow-sm hover:bg-gray-100 transition duration-300 gap-3"
            />
        </Link>
    );
}

const RESERVE_ROWS = 15;
const SHIMMER_ROWS = 1;

function ChannelAside({isMobile, width, setWidth}) {
    const [showCreateChannelForm, setShowCreateChannelForm] = useState(false);
    const [rowHeight, setRowHeight] = useState(0);
    const rowHeightAlreadyKnown = useRef(false);
    const listRef = useRef();

    const {
        channels,
        errorMessage,
        hasNextPage,
        fetchNextPage,
        isFetchingNextPage,
        isLoading,
        isError,
        refetch,
    } = useSubscribedChannels();


    const measureHeight = useCallback((node) => {
        if(node) {
           const height = node.offsetHeight;

           if(height > 0 && !rowHeightAlreadyKnown.current) {
               setRowHeight(height);
               rowHeightAlreadyKnown.current = true;
           }
        }
    }, []);

    const handleRowsRendered = useCallback((visibleRows) => {
        if (visibleRows && hasNextPage && !isFetchingNextPage && visibleRows.stopIndex >= channels.length - RESERVE_ROWS) {
            fetchNextPage();
        }
    }, [hasNextPage, isFetchingNextPage, channels.length, fetchNextPage]);

    const noChannel = () => {
        return <div className="flex flex-1 items-center justify-center px-6">
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
    };

    const calculateRowHeight = () => {
        const channel = channels[0];

        if(!channel) {
            return;
        }

        return <Link
            key={channel.channelId}
            to={channel.channelId}
            className="pointer-events-none absolute -z-10 left-0 top-0 invisible w-72"
            ref={measureHeight}
        >
            <Channel
                channel={channel}
                className="flex px-4 py-2 shadow-sm hover:bg-gray-100 transition duration-300 gap-3"
            />
        </Link>
    }

    const renderChannelList = () => {
        return <List
            className="flex-1 min-h-0"
            rowComponent={ChannelRow}
            rowCount={channels.length + (isFetchingNextPage ? SHIMMER_ROWS: 0)}
            rowHeight={rowHeight}
            rowProps={{ channels, isFetchingNextPage }}
            ref={listRef}
            onRowsRendered={handleRowsRendered}
        />
    };

    const errorContent = () => {
        return <div className="flex flex-1 items-center justify-center p-6">
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
    };

    return (
        <>
            <div className="relative flex h-full min-h-0 flex-col">
                {!isLoading && isError && errorContent()}

                {!isLoading && !isError && channels.length === 0 && noChannel()}

                {!isLoading && !isError && channels.length > 0 && calculateRowHeight()}

                {!isLoading && !isError && channels.length > 0 && rowHeight > 0 && renderChannelList()}

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