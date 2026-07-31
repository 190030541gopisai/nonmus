import { apiClient } from "../../../api/client";

const PREFIX = "/v1/users"

export const getCurrentUserApi  = async () => {
    const response = await apiClient.get(`${PREFIX}/me`);
    return response.data
};

export const updateUserApi = async (data) => {
    const response = await apiClient.put(PREFIX, data);
    return response.data
};
