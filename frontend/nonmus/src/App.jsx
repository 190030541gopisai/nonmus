import LoginPage from "./features/auth/pages/LoginPage";
import SignUpPage from "./features/auth/pages/SignUpPage";
import DashboardPage from "./features/auth/pages/DashboardPage";
import ProtectedRoute from "./routes/ProtectedRoute";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {PublicRoute} from "./routes/PublicRoute.jsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route
                    path="/"
                    element={
                        <ProtectedRoute>
                            <DashboardPage/>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/login"
                    element={
                        <PublicRoute>
                            <LoginPage/>
                        </PublicRoute>
                    }
                />
                <Route path="/signup" element={
                    <PublicRoute>
                        <SignUpPage/>
                    </PublicRoute>
                }/>

                <Route path="*" element={<Navigate to="/login" replace/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
