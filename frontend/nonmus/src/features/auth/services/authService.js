import {loginApi, logoutApi, refreshApi, signupApi} from "../api/authApi.js";
import {meApi} from "../api/userApi.js";

export async function fetchCurrentUser() {
    try {
        return await meApi();
    } catch {
        await refreshApi();
        return await meApi();
    }
}

export async function login(credentials) {
    await loginApi(credentials);
}

export async function signup(data) {
    await signupApi(data);
}

export async function logout() {
    await logoutApi();
}
