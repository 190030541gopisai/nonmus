import {FaEdit} from "react-icons/fa";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useState} from "react";

function NormalProfile({setEditProfile}) {
    const [showPreview, setShowPreview] = useState(false);

    const {user} = useAuth();
    const {viewUrl,  handleViewUrlExpired} = useProfilePicture();

    return <>
        <button
            type="button"
            onClick={() => setShowPreview(true)}
            className="mx-auto block"
        >
            <img
                src={viewUrl || user?.profilePicture}
                alt="Profile picture"
                className="h-24 w-24 rounded-full object-cover cursor-pointer lg:h-28 lg:w-28"
                onError={handleViewUrlExpired}
                referrerPolicy="no-referrer"
            />
        </button>
        <div className="flex-1 text-center pg-4">
            <div>
                <h1 className="text-xl font-bold lg:text-2xl">{user?.name || "Name"}</h1>
                <p className=" p-2 text-gray-600">{user?.email || "Email"}</p>
                <button
                    className="mt-4 inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-slate-700 lg:mt-4"
                    onClick={() => setEditProfile(true)}
                >
                    <FaEdit/> Edit Profile
                </button>
            </div>
        </div>

        {showPreview && (
            <div
                className="fixed inset-0 z-50 flex items-center justify-center bg-black/80"
                onClick={() => setShowPreview(false)}
            >
                <button
                    type="button"
                    className="absolute top-4 right-4 text-white text-3xl hover:text-gray-300 z-10"
                    onClick={() => setShowPreview(false)}
                >
                    &times;
                </button>
                <img
                    src={viewUrl}
                    alt="Profile picture"
                    className="max-w-[90vw] max-h-[90vh] rounded-lg object-contain"
                    onClick={(e) => e.stopPropagation()}
                />
            </div>
        )}
    </>;
}

export default NormalProfile;