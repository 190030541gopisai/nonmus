import {AuthContext} from "./authContext.js";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {getCurrentUserApi} from "../api/userApi.js";
import {logoutApi} from "../api/authApi.js";

export function AuthProvider({children}) {
    const queryClient = useQueryClient();

    const {
        data: user,
        refetch,
        isLoading,
        error,
        isFetching
    } = useQuery({
        queryKey: ["user"],
        queryFn: getCurrentUserApi,
        staleTime: 1000 * 60 * 5, // Data is fresh for 5 mins (prevents spamming API)
        retry: false
    });

    const refetchUser = () => refetch();

    const {mutateAsync:logout} = useMutation({
        mutationFn: logoutApi,
        onSuccess: (data) => {
            queryClient.clear();
        }
    });

    return (
        <AuthContext.Provider value={{user, isLoading, isAuthenticated: !!user, error, refetchUser, logout}}>
            {children}
        </AuthContext.Provider>
    );
}
