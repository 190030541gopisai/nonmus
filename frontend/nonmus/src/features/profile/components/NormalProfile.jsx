import {FaEdit} from "react-icons/fa";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useState} from "react";

function NormalProfile({setEditProfile}) {
    const [showPreview, setShowPreview] = useState(false);

    const {user} = useAuth();
    const {viewUrl,  handleViewUrlExpired} = useProfilePicture();

    return <>
        <div className="flex flex-col justify-center items-center pt-8 space-y-4">
            <button
                type="button"
                onClick={() => setShowPreview(true)}
            >
                <img
                    src={viewUrl || user?.profilePicture}
                    alt="Profile picture"
                    onError={handleViewUrlExpired}
                    referrerPolicy="no-referrer"
                    className="w-24 h-24 rounded-full"
                />
            </button>
            <div className="text-center">
                <div className="max-w-[min(90vw,320px)]">
                    <h1 className="truncate text-2xl text-slate-900">{user?.name || "Name"}</h1>
                    <p className="truncate text-sm text-blue-700">{user?.email || "Email"}</p>
                    <button
                        onClick={() => setEditProfile(true)}
                        className="mt-4 inline-flex justify-center items-center gap-3"
                    >
                        <FaEdit/> Edit Profile
                    </button>
                </div>
            </div>
        </div>

        {showPreview && (
            <div
                onClick={() => setShowPreview(false)}
                className="fixed inset-0 z-40 flex justify-center items-center bg-black/80"
            >
                <button
                    type="button"
                    onClick={() => setShowPreview(false)}
                    className="absolute top-4 right-4 text-white text-3xl hover:text-gray-300 z-10"
                >
                    &times;
                </button>
                <img
                    src={viewUrl}
                    alt="Profile picture"
                    onClick={(e) => e.stopPropagation()}
                />
            </div>
        )}
    </>;
}

export default NormalProfile;