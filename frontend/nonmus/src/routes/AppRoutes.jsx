import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";

import ProtectedRoute from "./ProtectedRoute.jsx";
import ForgotPasswordPage from "../features/auth/pages/ForgotPasswordPage.jsx";
import LoginPage from "../features/auth/pages/LoginPage.jsx";
import SignUpPage from "../features/auth/pages/SignUpPage.jsx";
import VerifyEmailPage from "../features/auth/pages/VerifyEmailPage.jsx";
import HomeLayout from "../features/home/layout/HomeLayout.jsx";
import HomePage from "../features/home/pages/HomePage.jsx";
import ReelsPage from "../features/reels/pages/ReelsPage.jsx";
import ChannelsPage from "../features/channels/pages/ChannelsPage.jsx";
import ChannelContent from "../features/channels/components/ChannelContent.jsx";
import ManageInvites from "../features/channels/components/ManageInvites.jsx";
import JoinInvitePage from "../features/channels/pages/JoinInvitePage.jsx";
import ProfilePage from "../features/profile/pages/ProfilePage.jsx";
import VideosPage from "../features/videos/pages/VideosPage.jsx";
import EditProfile from "../features/profile/components/EditProfile.jsx";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<ProtectedRoute/>}>
                    <Route path="/" element={<HomeLayout />}>
                        <Route index element={<HomePage />} />
                        <Route path="/videos" element={<VideosPage />} />
                        <Route path="/reels" element={<ReelsPage />} />
                        <Route path="/channels" element={<ChannelsPage />} >
                            <Route index element={<ChannelContent />} />
                            <Route path=":id" element={<ChannelContent />} />
                            <Route path=":id/invites" element={<ManageInvites />} />
                        </Route>
                        <Route path="/profile" element={<ProfilePage />} />
                    </Route>
                    <Route path="/invite/:token" element={<JoinInvitePage />} />
                </Route>

                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/signup" element={<SignUpPage/>}/>
                <Route path="/forgot-password" element={<ForgotPasswordPage/>}/>
                <Route path="/verify-email" element={<VerifyEmailPage/>}/>
                <Route path="/demo" element={<EditProfile />} />

                <Route path="*" element={<Navigate to="/login" replace/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;
