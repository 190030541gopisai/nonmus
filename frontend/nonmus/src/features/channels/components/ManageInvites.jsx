import {useEffect, useState} from "react";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {useNavigate, useParams} from "react-router-dom";
import {FaCheckCircle, FaLink, FaPen, FaShareAlt, FaTimes} from "react-icons/fa";
import {IoArrowBackCircleOutline} from "react-icons/io5";
import {
    buildInviteLink,
    CHANNEL_INVITES_QUERY_KEY,
    createChannelInviteApi,
    listChannelInvitesApi,
    updateChannelInviteApi,
} from "../api/inviteApi.js";
import {CiCirclePlus} from "react-icons/ci";

const JOIN_TYPES = [
    {value: "ANYONE", label: "Anyone with the link"},
    {value: "PASSWORD", label: "Password required"},
];

function formatUses(invite) {
    if (invite.maxUses == null) return `${invite.currentUses} used · no limit`;
    return `${invite.currentUses}/${invite.maxUses} used`;
}

function formatExpiry(expiry) {
    if (!expiry) return "Never expires";
    const d = new Date(expiry);
    return `Expires ${d.toLocaleDateString()} ${d.toLocaleTimeString([], {hour: "2-digit", minute: "2-digit"})}`;
}

function isExpired(expiry) {
    return expiry != null && new Date(expiry).getTime() <= Date.now();
}

function toLocalInputValue(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    const pad = (n) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function toIsoInstant(value) {
    return value ? new Date(value).toISOString() : null;
}

function CopyButton({text}) {
    const [copied, setCopied] = useState(false);

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(text);
            setCopied(true);
            setTimeout(() => setCopied(false), 1500);
        } catch {
            // clipboard unavailable
        }
    };

    return (
        <button
            type="button"
            onClick={handleCopy}
            className="inline-flex items-center gap-1.5 rounded-lg bg-blue-600 px-3 py-2 text-sm font-medium text-white transition hover:bg-blue-700 active:scale-95"
        >
            {copied ? <FaCheckCircle className="h-4 w-4"/> : <FaLink className="h-4 w-4"/>}
            {copied ? "Copied!" : "Copy"}
        </button>
    );
}

function ShareButton({text, title}) {
    const handleShare = async () => {
        if (navigator.share) {
            try {
                await navigator.share({title, url: text});
                return;
            } catch {
                // user cancelled share sheet
            }
        }
        try {
            await navigator.clipboard.writeText(text);
        } catch {
            // clipboard unavailable
        }
    };

    return (
        <button
            type="button"
            onClick={handleShare}
            className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100 active:scale-95"
        >
            <FaShareAlt className="h-4 w-4"/>
            Share
        </button>
    );
}

function CreateInviteForm({channelId, onCreated}) {
    const queryClient = useQueryClient();

    const [joinType, setJoinType] = useState("ANYONE");
    const [expiry, setExpiry] = useState("");
    const [maxUses, setMaxUses] = useState("");
    const [password, setPassword] = useState("");

    const {
        mutate: createInvite,
        isPending,
        error,
        isError,
    } = useMutation({
        mutationFn: () =>
            createChannelInviteApi({
                channelId,
                expiry: toIsoInstant(expiry),
                maxUses: maxUses ? Number(maxUses) : null,
                joinType,
                password: joinType === "PASSWORD" ? password : null,
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: CHANNEL_INVITES_QUERY_KEY(channelId)});
            setExpiry("");
            setMaxUses("");
            setPassword("");
            setJoinType("ANYONE");
            onCreated?.();
        },
    });

    const errorMessage = error?.response?.data?.message || error?.message || "";

    const handleSubmit = (e) => {
        e.preventDefault();
        createInvite();
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <label className="mb-1 block text-sm font-medium text-slate-700">
                    Who can join with this link?
                </label>
                <div className="space-y-2">
                    {JOIN_TYPES.map(({value, label}) => (
                        <label
                            key={value}
                            className={`flex cursor-pointer items-center gap-3 rounded-lg border p-3 transition ${
                                joinType === value
                                    ? "border-blue-500 bg-blue-50"
                                    : "border-slate-200 hover:border-slate-300"
                            }`}
                        >
                            <input
                                type="radio"
                                name="joinType"
                                value={value}
                                checked={joinType === value}
                                onChange={() => setJoinType(value)}
                                className="h-4 w-4 text-blue-600"
                            />
                            <span className="text-sm text-slate-800">{label}</span>
                        </label>
                    ))}
                </div>
            </div>

            {joinType === "PASSWORD" && (
                <div>
                    <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor="invite-password">
                        Password
                    </label>
                    <input
                        id="invite-password"
                        type="text"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password required to join"
                        className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                        required
                    />
                </div>
            )}

            <div className="grid gap-4 sm:grid-cols-2">
                <div>
                    <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor="invite-expiry">
                        Expiry <span className="font-normal text-slate-400">(optional)</span>
                    </label>
                    <input
                        id="invite-expiry"
                        type="datetime-local"
                        value={expiry}
                        onChange={(e) => setExpiry(e.target.value)}
                        className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                    />
                </div>
                <div>
                    <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor="invite-max-uses">
                        Max uses <span className="font-normal text-slate-400">(optional)</span>
                    </label>
                    <input
                        id="invite-max-uses"
                        type="number"
                        min="1"
                        value={maxUses}
                        onChange={(e) => setMaxUses(e.target.value)}
                        placeholder="No limit"
                        className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                    />
                </div>
            </div>

            {isError && (
                <p role="alert" className="text-sm text-red-500">
                    {errorMessage}
                </p>
            )}

            <button
                type="submit"
                disabled={isPending}
                className="w-full rounded-lg bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:opacity-50"
            >
                {isPending ? "Creating..." : "Create invite link"}
            </button>
        </form>
    );
}

function EditInviteForm({invite, channelId, onDone}) {
    const queryClient = useQueryClient();

    const [joinType, setJoinType] = useState(invite.joinType);
    const [expiry, setExpiry] = useState(toLocalInputValue(invite.expiry));
    const [maxUses, setMaxUses] = useState(invite.maxUses != null ? String(invite.maxUses) : "");
    const [password, setPassword] = useState("");

    const {
        mutate: updateInvite,
        isPending,
        error,
        isError,
    } = useMutation({
        mutationFn: () =>
            updateChannelInviteApi(invite.inviteId, {
                expiry: toIsoInstant(expiry),
                maxUses: maxUses ? Number(maxUses) : null,
                joinType,
                password: joinType === "PASSWORD" ? password : null,
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: CHANNEL_INVITES_QUERY_KEY(channelId)});
            onDone();
        },
    });

    const errorMessage = error?.response?.data?.message || error?.message || "";

    const handleSubmit = (e) => {
        e.preventDefault();
        updateInvite();
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
                {JOIN_TYPES.map(({value, label}) => (
                    <label
                        key={value}
                        className={`flex cursor-pointer items-center gap-3 rounded-lg border p-3 transition ${
                            joinType === value
                                ? "border-blue-500 bg-blue-50"
                                : "border-slate-200 bg-white hover:border-slate-300"
                        }`}
                    >
                        <input
                            type="radio"
                            name={`joinType-${invite.inviteId}`}
                            value={value}
                            checked={joinType === value}
                            onChange={() => setJoinType(value)}
                            className="h-4 w-4 text-blue-600"
                        />
                        <span className="text-sm text-slate-800">{label}</span>
                    </label>
                ))}
            </div>

            {joinType === "PASSWORD" && (
                <div>
                    <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor={`password-${invite.inviteId}`}>
                        {invite.joinType === "PASSWORD" ? "New password" : "Password"}
                    </label>
                    <input
                        id={`password-${invite.inviteId}`}
                        type="text"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password required to join"
                        className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                        required={joinType === "PASSWORD"}
                    />
                </div>
            )}

            <div className="grid gap-4 sm:grid-cols-2">
                <div>
                    <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor={`expiry-${invite.inviteId}`}>
                        Expiry <span className="font-normal text-slate-400">(blank = never)</span>
                    </label>
                    <input
                        id={`expiry-${invite.inviteId}`}
                        type="datetime-local"
                        value={expiry}
                        onChange={(e) => setExpiry(e.target.value)}
                        className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                    />
                </div>
                <div>
                    <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor={`max-uses-${invite.inviteId}`}>
                        Max uses <span className="font-normal text-slate-400">(blank = no limit)</span>
                    </label>
                    <input
                        id={`max-uses-${invite.inviteId}`}
                        type="number"
                        min="1"
                        value={maxUses}
                        onChange={(e) => setMaxUses(e.target.value)}
                        placeholder="No limit"
                        className="w-full rounded-lg border border-slate-300 p-3 text-sm focus:border-blue-500 focus:outline-none"
                    />
                </div>
            </div>

            {isError && (
                <p role="alert" className="text-sm text-red-500">
                    {errorMessage}
                </p>
            )}

            <div className="flex justify-end gap-3">
                <button
                    type="button"
                    onClick={onDone}
                    disabled={isPending}
                    className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 transition hover:bg-white disabled:opacity-50"
                >
                    Cancel
                </button>
                <button
                    type="submit"
                    disabled={isPending}
                    className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:opacity-50"
                >
                    {isPending ? "Saving..." : "Save changes"}
                </button>
            </div>
        </form>
    );
}

function InviteCard({invite, channelId}) {
    const [editing, setEditing] = useState(false);
    const link = buildInviteLink(invite.token);
    const expired = isExpired(invite.expiry);
    const maxedOut = invite.maxUses != null && invite.currentUses >= invite.maxUses;

    return (
        <div className={`rounded-xl border bg-white p-4 shadow-sm ${expired || maxedOut ? "border-amber-200" : "border-slate-200"}`}>
            <div className="flex flex-wrap items-start justify-between gap-3">
                <div className="min-w-0 flex-1">
                    <div className="mb-1 flex flex-wrap items-center gap-2">
                        <span className="rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-semibold text-blue-700">
                            {invite.joinType === "PASSWORD" ? "Password required" : "Anyone with the link"}
                        </span>
                        {expired && (
                            <span className="rounded-full bg-amber-100 px-2.5 py-0.5 text-xs font-semibold text-amber-700">
                                Expired
                            </span>
                        )}
                        {maxedOut && (
                            <span className="rounded-full bg-amber-100 px-2.5 py-0.5 text-xs font-semibold text-amber-700">
                                Max uses reached
                            </span>
                        )}
                    </div>
                    <p className="text-sm text-slate-500">{formatExpiry(invite.expiry)} · {formatUses(invite)}</p>
                </div>
            </div>

            <div className="mt-3 flex items-center gap-2">
                <p className="min-w-0 flex-1 truncate rounded-lg bg-slate-100 px-3 py-2.5 font-mono text-sm text-slate-800" title={link}>
                    {link}
                </p>
            </div>
            <div className="flex shrink-0 items-center gap-2">
                <ShareButton text={link} title={`Join ${invite.channelName ?? "channel"}`}/>
                <CopyButton text={link}/>
                <button
                    type="button"
                    onClick={() => setEditing((v) => !v)}
                    className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100 active:scale-95"
                >
                    <FaPen className="h-3.5 w-3.5"/>
                    {editing ? "Close" : "Edit"}
                </button>
            </div>

            {editing && <EditInviteModal invite={invite} channelId={channelId} onClose={() => setEditing(false)}/>}
        </div>
    );
}

function EditInviteModal({invite, channelId, onClose}) {
    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === "Escape") onClose();
        };
        document.addEventListener("keydown", handleKeyDown);
        document.body.style.overflow = "hidden";
        return () => {
            document.removeEventListener("keydown", handleKeyDown);
            document.body.style.overflow = "";
        };
    }, [onClose]);

    return (
        <div
            className="fixed inset-0 z-50 flex items-end justify-center bg-black/40 md:items-center"
            onClick={onClose}
            role="dialog"
            aria-modal="true"
            aria-label="Edit invite link"
        >
            <div
                className="max-h-[90vh] w-full overflow-y-auto rounded-t-2xl bg-white p-5 shadow-xl md:w-[480px] md:rounded-2xl md:p-6"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="mb-4 flex items-center justify-between">
                    <h2 className="text-xl font-semibold text-slate-900">Edit invite link</h2>
                    <button
                        type="button"
                        onClick={onClose}
                        aria-label="Close edit dialog"
                        className="inline-flex rounded-lg p-2 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
                    >
                        <FaTimes className="h-5 w-5"/>
                    </button>
                </div>
                <EditInviteForm invite={invite} channelId={channelId} onDone={onClose}/>
            </div>
        </div>
    );
}

function CreateInviteModal({channelId, onClose}) {
    useEffect(() => {
        const handleKeyDown = (e) => {
            if (e.key === "Escape") onClose();
        };
        document.addEventListener("keydown", handleKeyDown);
        document.body.style.overflow = "hidden";
        return () => {
            document.removeEventListener("keydown", handleKeyDown);
            document.body.style.overflow = "";
        };
    }, [onClose]);

    return (
        <div
            className="fixed inset-0 z-50 flex items-end justify-center bg-black/40 md:items-center"
            onClick={onClose}
            role="dialog"
            aria-modal="true"
            aria-label="Create a new invite link"
        >
            <div
                className="flex max-h-[90vh] w-full flex-col overflow-y-auto rounded-t-2xl bg-white p-5 shadow-xl md:w-[480px] md:rounded-2xl md:p-6"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="mb-4 flex items-center justify-between">
                    <h2 className="text-xl font-semibold text-slate-900">Create a new invite link</h2>
                    <button
                        type="button"
                        onClick={onClose}
                        aria-label="Close invite dialog"
                        className="inline-flex rounded-lg p-2 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
                    >
                        <FaTimes className="h-5 w-5"/>
                    </button>
                </div>
                <CreateInviteForm channelId={channelId} onCreated={onClose}/>
            </div>
        </div>
    );
}

function ManageInvites() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isCreateOpen, setIsCreateOpen] = useState(false);

    const {
        data: invites,
        isLoading,
        isError,
        error,
        refetch,
    } = useQuery({
        queryKey: CHANNEL_INVITES_QUERY_KEY(id),
        queryFn: () => listChannelInvitesApi(id),
        enabled: !!id,
    });

    const errorMessage = error?.response?.data?.message || error?.message || "Failed to load invites";

    if (!id) {
        return (
            <div className="flex h-full items-center justify-center p-6">
                <p className="text-sm text-slate-500">No channel selected.</p>
            </div>
        );
    }

    return (
        <div className="relative mx-auto w-full md:px-8">
            <button
                onClick={() => navigate(`/channels/${id}`)}
                aria-label="Go back"
                className="mb-4 inline-flex items-center gap-1 text-sm font-medium text-slate-600 transition hover:text-slate-900"
            >
                <IoArrowBackCircleOutline className="h-7 w-7"/>
                Back to channel
            </button>

            <h1 className="mb-1 text-2xl font-bold tracking-tight text-slate-900">Manage Invites</h1>
            <p className="mb-6 text-sm text-slate-500">
                Create shareable links so others can join this channel. Only the channel owner can manage invites.
            </p>

            <button
                type="button"
                onClick={() => setIsCreateOpen(true)}
                className="mb-6 inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 active:scale-95 sm:w-auto"
            >
                <CiCirclePlus className="h-5 w-5"/>
                Create Invite
            </button>

            <section>
                <h2 className="mb-3 text-lg font-semibold text-slate-900">Active invites</h2>

                {isLoading && (
                    <div className="space-y-3">
                        {Array.from({length: 2}).map((_, index) => (
                            <div key={index} className="h-32 animate-pulse rounded-xl bg-slate-100"/>
                        ))}
                    </div>
                )}

                {!isLoading && isError && (
                    <div className="flex flex-col items-center gap-3 rounded-xl border border-slate-200 bg-white p-6 text-center">
                        <p className="text-sm text-red-500">{errorMessage}</p>
                        <button
                            onClick={() => refetch()}
                            className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700"
                        >
                            Retry
                        </button>
                    </div>
                )}

                {!isLoading && !isError && invites?.length === 0 && (
                    <p className="rounded-xl border border-dashed border-slate-300 bg-white p-6 text-center text-sm text-slate-500">
                        No invites yet. Create one above to share your channel.
                    </p>
                )}

                {!isLoading && !isError && invites?.length > 0 && (
                    <div className="space-y-3">
                        {invites.map((invite) => (
                            <InviteCard key={invite.inviteId} invite={invite} channelId={id}/>
                        ))}
                    </div>
                )}
            </section>

            {isCreateOpen && <CreateInviteModal channelId={id} onClose={() => setIsCreateOpen(false)}/>}
        </div>
    );
}

export default ManageInvites;
