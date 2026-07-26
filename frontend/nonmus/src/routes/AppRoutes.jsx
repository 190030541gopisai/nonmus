import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";

import ProtectedRoute from "./ProtectedRoute.jsx";
import PublicRoute from "./PublicRoute.jsx";
import ForgotPasswordPage from "../features/auth/pages/ForgotPasswordPage.jsx";
import LoginPage from "../features/auth/pages/LoginPage.jsx";
import SignUpPage from "../features/auth/pages/SignUpPage.jsx";
import HomeLayout from "../features/home/layout/HomeLayout.jsx";
import HomePage from "../features/home/pages/HomePage.jsx";
import ReelsPage from "../features/reels/pages/ReelsPage.jsx";
import ChannelsPage from "../features/channels/pages/ChannelsPage.jsx";
import ProfilePage from "../features/profile/pages/ProfilePage.jsx";
import VideosPage from "../features/videos/pages/VideosPage.jsx";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<ProtectedRoute/>}>
                    {/*<Route path="/" element={<DashboardPage/>}/>*/}
                    <Route path="/" element={<HomeLayout />}>
                        <Route index element={<HomePage />} />
                        <Route path="/videos" element={<VideosPage />} />
                        <Route path="/reels" element={<ReelsPage />} />
                        <Route path="/channels" element={<ChannelsPage />} />
                        <Route path="/profile" element={<ProfilePage />} />
                    </Route>
                </Route>

                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/signup" element={<SignUpPage/>}/>
                <Route path="/forgot-password" element={<ForgotPasswordPage/>}/>

                <Route path="*" element={<Navigate to="/login" replace/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;
