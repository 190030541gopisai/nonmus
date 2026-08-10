import {useEffect, useState} from "react";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";
import {updateUserApi} from "../../auth/api/userApi.js";
import {useMutation, useQueryClient} from "@tanstack/react-query";

function EditProfile({setEditProfile}) {
    const queryClient = useQueryClient();

    const [name, setName] = useState("");
    const [profilePictureFile, setProfilePictureFile] = useState(null);
    const [previewProfilePictureFile, setPreviewProfilePictureFile] = useState(null);

    const {user} = useAuth();
    const {
        viewUrl,
        uploading,
        error,
        handleViewUrlExpired,
        uploadProfilePicture
    } = useProfilePicture();

    const imageSrc = previewProfilePictureFile || viewUrl;

    const handleFileChange = (e) => {
        const file = e.target.files?.[0];
        if (file) {
            setProfilePictureFile(file);
            setPreviewProfilePictureFile(URL.createObjectURL(file));
        }
    };

    const {mutateAsync: updateUser} = useMutation({
        mutationFn: (updatedUserDetails) => updateUserApi(updatedUserDetails),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ["user"],
            });
        },
    });

    const handleSubmit = async () => {
        try {
            await updateUser({name});
        } catch {
            if (user) setName(user.name || "");
        }

        if (profilePictureFile) {
            await uploadProfilePicture(profilePictureFile);
        }
        setEditProfile(false);
    };

    const handleCancel = () => {
        setEditProfile(false);
    };

    useEffect(() => {
        setName(user?.name);
    }, []);

    return <div className="pt-8">
        <label htmlFor="profile-picture-file-input" className="flex justify-center items-center cursor-pointer">
            <img
                src={imageSrc}
                alt="Profile picture"
                onError={handleViewUrlExpired}
                className="h-24 w-24 rounded-full opacity-50"
            />
            <span className="absolute opacity-100">
              <svg viewBox="0 0 24 24" className="h-7 w-7">
                <path d="M12 15.2A3.2 3.2 0 1 0 12 8.8a3.2 3.2 0 0 0 0 6.4z"/>
                <path
                    d="M9 2L7.17 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2h-3.17L15 2H9zm3 15a5 5 0 1 1 0-10 5 5 0 0 1 0 10z"/>
              </svg>
            </span>
            <input
                id="profile-picture-file-input"
                type="file"
                accept="image/jpeg,image/png,image/webp,image/gif"
                aria-hidden="true"
                onChange={handleFileChange}
                className="hidden"
            />
        </label>
        {uploading && <p className="mt-2 text-center text-xs text-blue-500">Uploading...</p>}
        <div className="flex flex-col justify-center items-center m-4">
            <div className="text-center">
                <input
                    type="text"
                    placeholder="Name"
                    className="block w-full rounded-lg border border-slate-300 px-3 py-2 text-lg font-bold focus:border-slate-500 focus:outline-none"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    autoFocus
                />
                <p className="truncate text-sm text-gray-400">{user?.email || "Email"}</p>
            </div>
            <div className="mt-4 flex justify-center gap-3 lg:justify-start">
                <button
                    disabled={uploading}
                    className="rounded-lg bg-blue-500 px-4 py-2 text-sm font-medium text-white hover:bg-blue-600"
                    onClick={handleSubmit}
                >
                    {uploading ? "Saving...": "Save"}
                </button>
                <button
                    disabled={uploading}
                    className="rounded-lg bg-gray-500 px-4 py-2 text-sm font-medium text-white hover:bg-gray-600 disabled:opacity-50"
                    onClick={handleCancel}>Cancel
                </button>
            </div>
            {error && <p className="mt-2 text-center text-xs text-red-500">{error}</p>}
        </div>
    </div>
}

export default EditProfile;