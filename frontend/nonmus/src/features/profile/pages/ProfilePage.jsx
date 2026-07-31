import {TbLogout} from "react-icons/tb";
import {useEffect, useState} from "react";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";
import NormalProfile from "../components/NormalProfile.jsx";
import EditProfile from "../components/EditProfile.jsx";

function ProfilePage() {
    const {logout} = useAuth();

    const {uploading, error, loadViewUrl} = useProfilePicture();

    const [editProfile, setEditProfile] = useState(false);

    useEffect(() => {
        loadViewUrl();
    }, [loadViewUrl]);

    const handleLogout = async () => {
        await logout();
    }

    return (
        <div className="mx-auto max-w-2xl px-4 py-6 lg:py-10">
            <div className="relative rounded-2xl border border-slate-200 bg-white p-4 lg:p-8">
                <div className="flex flex-col items-center gap-6 lg:flex-row lg:items-start lg:justify-center">
                    <div className="flex-shrink-0">
                        {editProfile ?
                            <EditProfile setEditProfile={setEditProfile} /> :
                            <NormalProfile setEditProfile={setEditProfile} />
                        }
                    </div>
                </div>
                <div className="mt-6 border-t border-slate-200 pt-4 lg:hidden">
                    <button
                        type="button"
                        onClick={handleLogout}
                        className="flex w-full items-center justify-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium text-slate-500 transition hover:bg-red-50 hover:text-red-600"
                    >
                        <TbLogout size={18}/>
                        Logout
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ProfilePage;
