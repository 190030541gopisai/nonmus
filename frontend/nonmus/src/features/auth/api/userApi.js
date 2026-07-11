import { apiClient } from "../../../api/client";

const PREFIX = "/v1/users"

export const meApi = async () => {
    const response = await apiClient.get(`${PREFIX}/me`);
    return response.data
};
