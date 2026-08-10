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

function PageBackground({children}) {
    return (
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-100 via-white to-indigo-100 px-4 py-12">
            {children}
        </div>
    );
}

function Spinner({className = ""}) {
    return (
        <svg className={`h-5 w-5 animate-spin ${className}`} viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
        </svg>
    );
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
            <PageBackground>
                <div className="flex flex-col items-center gap-4">
                    <div className="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-blue-600"/>
                    <p className="text-sm font-medium text-slate-500">Loading invite...</p>
                </div>
            </PageBackground>
        );
    }

    if (isError) {
        return (
            <PageBackground>
                <div className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-xl shadow-slate-200/60">
                    <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-red-50 text-red-500">
                        <FaLock size={26}/>
                    </div>
                    <h1 className="mt-6 text-2xl font-bold text-slate-900">Invite unavailable</h1>
                    <p className="mt-3 text-sm leading-relaxed text-slate-500">{infoErrorMessage}</p>
                </div>
            </PageBackground>
        );
    }

    return (
        <PageBackground>
            <div className="w-full max-w-md">
                <form onSubmit={handleJoin} className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-xl shadow-slate-200/60">
                    <div className="flex flex-col items-center bg-gradient-to-b from-blue-50/60 to-white px-8 pb-8 pt-12 text-center">
                        <div className="group">
                            <ChannelLogo channel={{logo: invite.channelLogo, name: invite.channelName}} size="xl"/>
                        </div>
                        <h1 className="mt-6 text-2xl font-bold tracking-tight text-slate-900">
                            {invite.channelName}
                        </h1>
                        <p className="mt-1 text-sm text-slate-500">
                            You've been invited to join this channel
                        </p>

                        <div className="mt-6 flex max-w-full flex-wrap items-center justify-center gap-2">
                            <span className="inline-flex items-center gap-1.5 rounded-full bg-blue-50 px-3 py-1.5 text-xs font-medium text-blue-700">
                                <FaUsers/>
                                {formatUses(invite.currentUses, invite.maxUses)}
                            </span>
                            <span className="inline-flex items-center gap-1.5 rounded-full bg-emerald-50 px-3 py-1.5 text-xs font-medium text-emerald-700">
                                <FaCheckCircle/>
                                {formatExpiry(invite.expiry)}
                            </span>
                        </div>
                    </div>

                    <div className="h-px bg-slate-100"/>

                    <div className="px-8 py-6">
                        {invite.joinType === "PASSWORD" && (
                            <div className="space-y-2">
                                <label htmlFor="join-password" className="block text-sm font-medium text-slate-700">
                                    This invite is password protected
                                </label>
                                <input
                                    id="join-password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Enter the invite password"
                                    required
                                    className="w-full rounded-xl border border-slate-300 px-4 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
                                />
                            </div>
                        )}

                        {isJoinError && (
                            <p role="alert" className="mt-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">
                                {joinErrorMessage}
                            </p>
                        )}

                        <button
                            type="submit"
                            disabled={isPending}
                            className="mt-6 w-full rounded-xl bg-slate-800 py-3 text-sm font-semibold text-white transition hover:bg-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-800 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 disabled:hover:bg-slate-800"
                        >
                            {isPending ? (
                                <span className="flex items-center justify-center gap-2">
                                    <Spinner/>
                                    Joining...
                                </span>
                            ) : `Join ${invite.channelName}`}
                        </button>

                        <p className="mt-4 text-center text-xs text-slate-400">
                            By joining you agree to follow this channel's rules
                        </p>
                    </div>
                </form>
            </div>
        </PageBackground>
    );
}

export default JoinInvitePage;