import {useEffect, useState} from "react";
import {createPortal} from "react-dom";
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
import {getChannelByIdApi} from "../api/channelApi.js";

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
            className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
            >
            {copied
                ? <FaCheckCircle className="text-emerald-500"/>
                : <FaLink className="text-slate-400"/>}
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
            className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
            >
            <FaShareAlt className="text-slate-400"/>
            Share
        </button>
    );
}

const inputClass = "w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-800 placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transition";
const labelClass = "mb-1.5 block text-sm font-medium text-slate-700";
const fieldGroupClass = "space-y-6 sm:space-y-0 sm:grid sm:grid-cols-2 sm:gap-4";

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
        <form onSubmit={handleSubmit} className="space-y-6">
            <div>
                <span className="mb-2 block text-sm font-semibold text-slate-800">Who can join with this link?</span>
                <div className="grid gap-3 sm:grid-cols-2">
                    {JOIN_TYPES.map(({value, label}) => (
                        <label
                            key={value}
                            className={`flex cursor-pointer items-center gap-3 rounded-xl border px-4 py-3 transition ${
                                joinType === value
                                    ? "border-blue-500 bg-blue-50 ring-2 ring-blue-500/20"
                                    : "border-slate-300 bg-white hover:border-slate-400"
                            }`}
                            >
                            <input
                                type="radio"
                                name="joinType"
                                value={value}
                                checked={joinType === value}
                                onChange={() => setJoinType(value)}
                                className="h-4 w-4 accent-blue-600"
                                />
                            <span className="text-sm font-medium text-slate-700">{label}</span>
                        </label>
                    ))}
                </div>
            </div>

            {joinType === "PASSWORD" && (
                <div>
                    <label htmlFor="invite-password" className={labelClass}>
                        Password
                    </label>
                    <input
                        id="invite-password"
                        type="text"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password required to join"
                        required
                        className={inputClass}
                    />
                </div>
            )}

            <div className={fieldGroupClass}>
                <div>
                    <label htmlFor="invite-expiry" className={labelClass}>
                        Expiry <span className="font-normal text-slate-400">(optional)</span>
                    </label>
                    <input
                        id="invite-expiry"
                        type="datetime-local"
                        value={expiry}
                        onChange={(e) => setExpiry(e.target.value)}
                        className={inputClass}
                        />
                </div>
                <div>
                    <label htmlFor="invite-max-uses" className={labelClass}>
                        Max uses <span className="font-normal text-slate-400">(optional)</span>
                    </label>
                    <input
                        id="invite-max-uses"
                        type="number"
                        min="1"
                        value={maxUses}
                        onChange={(e) => setMaxUses(e.target.value)}
                        placeholder="No limit"
                        className={inputClass}
                        />
                </div>
            </div>

            {isError && (
                <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600">
                    {errorMessage}
                </p>
            )}

            <button
                type="submit"
                disabled={isPending}
                className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
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
        <form onSubmit={handleSubmit} className="space-y-5">
            <div>
                <span className="mb-2 block text-sm font-semibold text-slate-800">Who can join with this link?</span>
                <div className="grid gap-3 sm:grid-cols-2">
                    {JOIN_TYPES.map(({value, label}) => (
                        <label
                            key={value}
                            className={`flex cursor-pointer items-center gap-3 rounded-xl border px-4 py-3 transition ${
                                joinType === value
                                    ? "border-blue-500 bg-blue-50 ring-2 ring-blue-500/20"
                                    : "border-slate-300 bg-white hover:border-slate-400"
                            }`}
                            >
                            <input
                                type="radio"
                                name={`joinType-${invite.inviteId}`}
                                value={value}
                                checked={joinType === value}
                                onChange={() => setJoinType(value)}
                                className="h-4 w-4 accent-blue-600"
                                />
                            <span className="text-sm font-medium text-slate-700">{label}</span>
                        </label>
                    ))}
                </div>
            </div>

            {joinType === "PASSWORD" && (
                <div>
                    <label htmlFor={`password-${invite.inviteId}`} className={labelClass}>
                        {invite.joinType === "PASSWORD" ? "New password" : "Password"}
                    </label>
                    <input
                        id={`password-${invite.inviteId}`}
                        type="text"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password required to join"
                        required={joinType === "PASSWORD"}
                        className={inputClass}
                    />
                </div>
            )}

            <div className={fieldGroupClass}>
                <div>
                    <label htmlFor={`expiry-${invite.inviteId}`} className={labelClass}>
                        Expiry <span className="font-normal text-slate-400">(blank = never)</span>
                    </label>
                    <input
                        id={`expiry-${invite.inviteId}`}
                        type="datetime-local"
                        value={expiry}
                        onChange={(e) => setExpiry(e.target.value)}
                        className={inputClass}
                        />
                </div>
                <div>
                    <label htmlFor={`max-uses-${invite.inviteId}`} className={labelClass}>
                        Max uses <span className="font-normal text-slate-400">(blank = no limit)</span>
                    </label>
                    <input
                        id={`max-uses-${invite.inviteId}`}
                        type="number"
                        min="1"
                        value={maxUses}
                        onChange={(e) => setMaxUses(e.target.value)}
                        placeholder="No limit"
                        className={inputClass}
                        />
                </div>
            </div>

            {isError && (
                <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600">
                    {errorMessage}
                </p>
            )}

            <div className="flex justify-end gap-3">
                <button
                    type="button"
                    onClick={onDone}
                    disabled={isPending}
                    className="rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:opacity-50"
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
    const unusable = expired || maxedOut;

    return (
        <div className={`rounded-xl border bg-white p-5 shadow-sm transition ${
            unusable ? "border-slate-200 opacity-80" : "border-slate-200 hover:border-slate-300 hover:shadow"
        }`}>
            <div className="flex items-start justify-between gap-4">
                <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2">
                        <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-700">
                            {invite.joinType === "PASSWORD" ? "Password required" : "Anyone with the link"}
                        </span>
                        {expired && (
                            <span className="inline-flex items-center rounded-full bg-red-100 px-2.5 py-0.5 text-xs font-medium text-red-700">
                                Expired
                            </span>
                        )}
                        {maxedOut && (
                            <span className="inline-flex items-center rounded-full bg-amber-100 px-2.5 py-0.5 text-xs font-medium text-amber-700">
                                Max uses reached
                            </span>
                        )}
                    </div>
                    <p className="mt-2 text-sm text-slate-500">{formatExpiry(invite.expiry)} · {formatUses(invite)}</p>
                </div>
            </div>

            <div className="mt-3 rounded-lg bg-slate-50 px-3 py-2.5">
                <p
                    title={link}
                    className="truncate font-mono text-xs text-slate-600"
                    >
                    {link}
                </p>
            </div>

            <div className="mt-4 flex flex-wrap gap-2">
                <ShareButton text={link} title={`Join ${invite.channelName ?? "channel"}`}/>
                <CopyButton text={link}/>
                <button
                    type="button"
                    onClick={() => setEditing((v) => !v)}
                    className={`inline-flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-sm font-medium transition ${
                        editing
                            ? "bg-slate-700 text-white hover:bg-slate-800"
                            : "border border-slate-300 bg-white text-slate-700 hover:bg-slate-50"
                    }`}
                    >
                    <FaPen className="text-xs"/>
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

    return createPortal(
        <div
            onClick={onClose}
            role="dialog"
            aria-modal="true"
            aria-label="Edit invite link"
            className="fixed inset-0 z-50 flex items-end justify-center bg-slate-900/60 backdrop-blur-sm md:items-center"
        >
            <div
                onClick={(e) => e.stopPropagation()}
                className="w-full rounded-t-2xl bg-white p-6 shadow-xl md:max-w-lg md:rounded-2xl"
            >
                <div className="mb-5 flex items-center justify-between">
                    <h2 className="text-lg font-semibold text-slate-900">Edit invite link</h2>
                    <button
                        type="button"
                        onClick={onClose}
                        aria-label="Close edit dialog"
                        className="rounded-full p-1.5 text-slate-400 transition hover:bg-slate-100 hover:text-slate-600"
                        >
                        <FaTimes/>
                    </button>
                </div>
                <EditInviteForm invite={invite} channelId={channelId} onDone={onClose}/>
            </div>
        </div>,
        document.body,
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

    return createPortal(
        <div
            onClick={onClose}
            role="dialog"
            aria-modal="true"
            aria-label="Create a new invite link"
            className="fixed inset-0 z-50 flex items-end justify-center bg-slate-900/60 backdrop-blur-sm md:items-center"
        >
            <div
                onClick={(e) => e.stopPropagation()}
                className="w-full rounded-t-2xl bg-white p-6 shadow-xl md:max-w-lg md:rounded-2xl"
            >
                <div className="mb-5 flex items-center justify-between">
                    <h2 className="text-lg font-semibold text-slate-900">Create a new invite link</h2>
                    <button
                        type="button"
                        onClick={onClose}
                        aria-label="Close invite dialog"
                        className="rounded-full p-1.5 text-slate-400 transition hover:bg-slate-100 hover:text-slate-600"
                        >
                        <FaTimes/>
                    </button>
                </div>
                <CreateInviteForm channelId={channelId} onCreated={onClose}/>
            </div>
        </div>,
        document.body,
    );
}

function ManageInvites() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isCreateOpen, setIsCreateOpen] = useState(false);

    const {data: channel, isLoading: channelLoading} = useQuery({
        queryKey: ["channel", id],
        queryFn: () => getChannelByIdApi(id),
        enabled: !!id,
    });

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

    if (channelLoading) {
        return <div className="flex min-h-[50vh] items-center justify-center p-6">
            <p className="text-sm text-slate-500">Loading...</p>
        </div>;
    }

    if (!channel?.owner) {
        return (
            <div className="flex min-h-[50vh] flex-col items-center justify-center gap-3 p-6 text-center">
                <p className="text-sm text-red-600">
                    Only the channel owner can manage invites.
                </p>
                <button
                    onClick={() => navigate(`/channels/${id}`)}
                    className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700">
                    Back to channel
                </button>
            </div>
        );
    }

    if (!id) {
        return (
            <div className="flex min-h-[50vh] items-center justify-center p-6">
                <p className="text-sm text-slate-500">No channel selected.</p>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-3xl px-4 py-8">
            <button
                onClick={() => navigate(`/channels/${id}`)}
                aria-label="Go back"
                className="mb-6 inline-flex items-center gap-1.5 text-sm font-medium text-slate-500 transition hover:text-slate-800"
                >
                <IoArrowBackCircleOutline className="text-xl"/>
                Back to channel
            </button>

            <div className="flex flex-wrap items-end justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Manage Invites</h1>
                    <p className="mt-1 max-w-xl text-sm text-slate-500">
                        Create shareable links so others can join this channel. Only the channel owner can manage invites.
                    </p>
                </div>

                <button
                    type="button"
                    onClick={() => setIsCreateOpen(true)}
                    className="inline-flex items-center gap-2 rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700"
                    >
                    <CiCirclePlus className="text-lg"/>
                    Create Invite
                </button>
            </div>

            <section className="mt-8">
                <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-slate-400">Active invites</h2>

                {isLoading && (
                    <div className="grid gap-4">
                        {Array.from({length: 2}).map((_, index) => (
                            <div key={index} className="h-32 animate-pulse rounded-xl bg-slate-100"/>
                        ))}
                    </div>
                )}

                {!isLoading && isError && (
                    <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center">
                        <p className="text-sm text-red-600">{errorMessage}</p>
                        <button
                            onClick={() => refetch()}
                            className="mt-3 rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-red-700"
                            >
                            Retry
                        </button>
                    </div>
                )}

                {!isLoading && !isError && invites?.length === 0 && (
                    <div className="rounded-xl border border-dashed border-slate-300 bg-slate-50 p-10 text-center">
                        <p className="text-sm text-slate-500">No invites yet. Create one above to share your channel.</p>
                    </div>
                )}

                {!isLoading && !isError && invites?.length > 0 && (
                    <div className="grid gap-4">
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