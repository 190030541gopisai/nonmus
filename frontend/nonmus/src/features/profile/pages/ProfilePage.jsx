import {TbLogout} from "react-icons/tb";
import {useEffect, useState} from "react";
import {useAuth} from "../../auth/hooks/useAuth.js";
import {useProfilePicture} from "../../auth/hooks/useProfilePicture.js";
import NormalProfile from "../components/NormalProfile.jsx";
import EditProfile from "../components/EditProfile.jsx";
import {HiOutlineDotsVertical} from "react-icons/hi";

function ProfilePage() {
    const {logout} = useAuth();
    const [showMenu, setShowMenu] = useState(false);

    const {loadViewUrl} = useProfilePicture();

    const [editProfile, setEditProfile] = useState(false);

    useEffect(() => {
        loadViewUrl();
    }, [loadViewUrl]);

    const handleLogout = async () => {
        await logout();
    }

    return (
        <div>
            <div className="relative">
                {editProfile ?
                    <EditProfile setEditProfile={setEditProfile}/> :
                    <NormalProfile setEditProfile={setEditProfile}/>
                }

                {/* More Options */}
                <div className="absolute right-4 top-4">
                    <button
                        type="button"
                        className="flex h-10 w-10 items-center justify-center rounded-full
                           text-gray-600 transition
                           hover:bg-gray-100 hover:text-gray-900
                           focus:outline-none focus:ring-2 focus:ring-gray-300"
                        onClick={() => setShowMenu(prev => !prev)}
                    >
                        <HiOutlineDotsVertical className="text-xl"/>
                    </button>

                    {/* Dropdown */}
                    {showMenu && (
                        <>
                            <div
                                className="fixed inset-0 z-20"
                                onClick={() => setShowMenu(false)}
                            />
                            <div
                                className="absolute right-5 top-12 z-50 w-44
                           overflow-hidden rounded-xl bg-white
                           shadow-lg"
                            >
                                <button
                                    type="button"
                                    onClick={handleLogout}
                                    className="flex w-full items-center gap-3 px-4 py-3
                               text-sm font-medium text-gray-700
                               transition hover:bg-gray-50 hover:text-red-600"
                                >
                                    <TbLogout className="text-lg"/>
                                    <span>Logout</span>
                                </button>
                            </div>
                        </>)}
                </div>
            </div>
        </div>
    );
}

export default ProfilePage;
