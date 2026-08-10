import {apiClient} from "../../../api/client";

const PREFIX = "/v1/channels/invite";

export const CHANNEL_INVITES_QUERY_KEY = (channelId) => ["channelInvites", channelId];

export const createChannelInviteApi = async (data) => {
    const response = await apiClient.post(PREFIX, data);
    return response.data;
};

export const getChannelInviteInfoApi = async (token) => {
    const response = await apiClient.get(`${PREFIX}/${token}`);
    return response.data;
};

export const listChannelInvitesApi = async (channelId) => {
    const response = await apiClient.get(`/v1/channels/${channelId}/invites`);
    return response.data;
};

export const joinChannelViaInviteApi = async (token, password) => {
    const response = await apiClient.post(`${PREFIX}/join/${token}`, {password: password || null});
    return response.data;
};

export const updateChannelInviteApi = async (inviteId, data) => {
    const response = await apiClient.put(`${PREFIX}/${inviteId}`, data);
    return response.data;
};

export const buildInviteLink = (token) => `${window.location.origin}/invite/${token}`;
