import {apiClient} from "../../../api/client";

const PREFIX = "/v1/channels";

export const CHANNELS_QUERY_KEY = ["subscribedChannels"];

export const createChannelApi = async (data) => {
    const response = await apiClient.post(PREFIX, data);
    return response.data;
};

export const getSubscribedChannelsApi = async ({cursor, limit = 20}) => {
    const response = await apiClient.get(`${PREFIX}/subscribed`, {
        params: {cursor: cursor || undefined, limit},
    });
    return response.data;
};

export const getChannelByIdApi = async (channelId) => {
    const response = await apiClient.get(`${PREFIX}/id/${channelId}`);
    return response.data;
};

export const getChannelByHandleApi = async (handle) => {
    const response = await apiClient.get(`${PREFIX}/handle/${handle}`);
    return response.data;
};

export const getHandleAvailabilityApi = async (handle) => {
    const response = await apiClient.get(`${PREFIX}/handle/${handle}/availability`);
    return response.data;
};
