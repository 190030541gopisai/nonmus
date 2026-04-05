import { createClient } from "./baseClient";

export const authClient = createClient("http://localhost:8003/api/v1/auth");
