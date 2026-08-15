import {useInfiniteQuery} from "@tanstack/react-query";
import {CHANNELS_QUERY_KEY, getSubscribedChannelsApi} from "../api/channelApi.js";
import {useMemo} from "react";

const PAGE_SIZE = 20;

export function useSubscribedChannels() {
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
    })

    const channels = useMemo(
        () => data?.pages.flatMap((page) => page.channels ?? []) ?? [],
        [data],
    );

    const errorMessage = error?.response?.data?.message || error?.message || "Failed to load channels";

    return {
        channels,
        errorMessage,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage,
        isLoading,
        isError,
        error,
        refetch,
    }
}