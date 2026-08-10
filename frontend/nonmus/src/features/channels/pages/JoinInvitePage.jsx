import {useState} from "react";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {useNavigate, useParams} from "react-router-dom";
import {FaCheckCircle, FaLock, FaUsers} from "react-icons/fa";
import {
    getChannelInviteInfoApi,
    joinChannelViaInviteApi,
} from "../api/inviteApi.js";
import {CHANNELS_QUERY_KEY} from "../api/channelApi.js";
import ChannelLogo from "../components/ChannelLogo.jsx";

function formatUses(currentUses, maxUses) {
    if (maxUses == null) return `${currentUses} joined`;
    return `${currentUses} of ${maxUses} joins used`;
}

function formatExpiry(expiry) {
    if (!expiry) return "This invite never expires";
    return `Invite expires ${new Date(expiry).toLocaleDateString()} ${new Date(expiry).toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
    })}`;
}

function JoinInvitePage() {
    const {token} = useParams();
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const [password, setPassword] = useState("");

    const {
        data: invite,
        isLoading,
        isError,
        error,
    } = useQuery({
        queryKey: ["inviteInfo", token],
        queryFn: () => getChannelInviteInfoApi(token),
        enabled: !!token,
        retry: false,
    });

    const {
        mutate: joinChannel,
        isPending,
        isError: isJoinError,
        error: joinError,
    } = useMutation({
        mutationFn: () => joinChannelViaInviteApi(token, password),
        onSuccess: (result) => {
            queryClient.invalidateQueries({queryKey: CHANNELS_QUERY_KEY});
            navigate(`/channels/${result.channelId}`);
        },
        onError: (err) => {
            if (err?.response?.status === 409 && invite?.channelId) {
                queryClient.invalidateQueries({queryKey: CHANNELS_QUERY_KEY});
                navigate(`/channels/${invite.channelId}`);
            }
        },
    });

    const infoErrorMessage = error?.response?.data?.message || error?.message || "This invite is not valid.";
    const joinErrorMessage = joinError?.response?.data?.message || joinError?.message || "Could not join the channel.";

    const handleJoin = (e) => {
        e.preventDefault();
        if (invite?.joinType === "PASSWORD" && !password) return;
        joinChannel();
    };

    if (isLoading) {
        return (
            <div className="flex h-full items-center justify-center p-6">
                <div className="w-full max-w-md animate-pulse space-y-4 rounded-2xl bg-white p-8 shadow-sm">
                    <div className="mx-auto h-28 w-28 rounded-full bg-slate-100"/>
                    <div className="mx-auto h-6 w-1/2 rounded bg-slate-100"/>
                    <div className="mx-auto h-4 w-1/3 rounded bg-slate-100"/>
                    <div className="h-12 w-full rounded-lg bg-slate-100"/>
                </div>
            </div>
        );
    }

    if (isError) {
        return (
            <div className="flex h-full items-center justify-center p-6">
                <div className="w-full max-w-md rounded-2xl bg-white p-8 text-center shadow-sm">
                    <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-red-100">
                        <FaLock className="text-xl text-red-500"/>
                    </div>
                    <h1 className="text-lg font-semibold text-slate-900">Invite unavailable</h1>
                    <p className="mt-2 text-sm text-slate-500">{infoErrorMessage}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="h-screen">
            <form onSubmit={handleJoin} className="w-full max-w-md rounded-2xl bg-white p-8 shadow-sm">
                <div className="flex flex-col items-center text-center">
                    <ChannelLogo channel={{logo: invite.channelLogo, name: invite.channelName}} size="xl"/>
                    <h1 className="mt-4 text-2xl font-bold tracking-tight text-slate-900">{invite.channelName}</h1>
                    <p className="mt-1 inline-flex items-center gap-1.5 text-sm text-slate-500">
                        <FaUsers className="h-4 w-4"/>
                        {formatUses(invite.currentUses, invite.maxUses)}
                    </p>
                    <p className="mt-1 inline-flex items-center gap-1.5 text-sm text-slate-500">
                        <FaCheckCircle className="h-4 w-4 text-green-500"/>
                        {formatExpiry(invite.expiry)}
                    </p>
                </div>

                <div className="my-6 border-t border-slate-100"/>

                {invite.joinType === "PASSWORD" && (
                    <div className="mb-4">
                        <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor="join-password">
                            This invite is password protected
                        </label>
                        <input
                            id="join-password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter the invite password"
                            className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                            required
                        />
                    </div>
                )}

                {isJoinError && (
                    <p role="alert" className="mb-4 text-sm text-red-500">
                        {joinErrorMessage}
                    </p>
                )}

                <button
                    type="submit"
                    disabled={isPending}
                    className="w-full rounded-lg bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:opacity-50"
                >
                    {isPending ? "Joining..." : `Join ${invite.channelName}`}
                </button>
            </form>
        </div>
    );
}

export default JoinInvitePage;
