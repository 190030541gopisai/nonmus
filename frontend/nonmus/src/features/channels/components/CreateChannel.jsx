import {useState} from "react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {createChannelApi, CHANNELS_QUERY_KEY} from "../api/channelApi.js";

function CreateChannel({setShowCreateChannelForm}) {
    const queryClient = useQueryClient();

    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [type, setType] = useState("PUBLIC");
    const [handle, setHandle] = useState("");

    const {
        mutate: createChannel,
        isPending,
        error,
    } = useMutation({
        mutationFn: () => createChannelApi({
            name,
            description,
            type,
            handle: type === "PUBLIC" ? handle : undefined,
        }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: CHANNELS_QUERY_KEY});
            setShowCreateChannelForm(false);
        },
    });

    const errorMessage = error?.response?.data?.message || error?.message || "";

    const handleSubmit = (e) => {
        e.preventDefault();
        createChannel();
    };

    return (
        <div className="fixed inset-0 z-50 bg-black/40 flex items-end md:items-center justify-center">
            <div
                className="
                            w-full
                            md:w-[500px]
                            bg-white
                            rounded-t-2xl md:rounded-2xl
                            p-6
                            max-h-[90vh]
                            overflow-y-auto
                        "
            >
                <h2 className="mb-4 text-xl font-semibold">
                    Create Channel
                </h2>

                <form className="space-y-4" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="name"
                        placeholder="Channel Name"
                        className="w-full rounded-lg border p-3"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />

                    <textarea
                        name="description"
                        placeholder="Description"
                        rows={3}
                        className="w-full rounded-lg border p-3"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />

                    <select
                        name="type"
                        className="w-full rounded-lg border p-3"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                    >
                        <option value="PRIVATE">Private</option>
                        <option value="PUBLIC">Public</option>
                    </select>

                    {type === "PUBLIC" && (
                        <input
                            type="text"
                            name="handle"
                            placeholder="Handle (e.g. gopisai)"
                            className="w-full rounded-lg border p-3"
                            value={handle}
                            onChange={(e) => setHandle(e.target.value)}
                            required
                        />
                    )}

                    {errorMessage && (
                        <p role="alert" className="text-sm text-red-500">
                            {errorMessage}
                        </p>
                    )}

                    <div className="flex justify-end gap-3">
                        <button
                            type="button"
                            onClick={() => setShowCreateChannelForm(false)}
                            disabled={isPending}
                            className="rounded-lg border px-4 py-2 disabled:opacity-50"
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            disabled={isPending}
                            className="rounded-lg bg-blue-600 px-4 py-2 text-white disabled:opacity-50"
                        >
                            {isPending ? "Creating..." : "Create"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateChannel;
