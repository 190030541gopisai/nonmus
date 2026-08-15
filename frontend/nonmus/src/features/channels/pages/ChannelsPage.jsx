import {useEffect, useState} from "react";
import {Outlet, useParams} from "react-router-dom";
import ChannelAside from "../components/ChannelAside.jsx";
import {useAuth} from "../../auth/hooks/useAuth.js";
import LoadingFallback from "../../../routes/LoadingFallback.jsx";

function ChannelsPage() {
    const {emailVerified, isLoading} = useAuth();

    const {id} = useParams();

    const [isMobile, setIsMobile] = useState(() => window.innerWidth < 768);

    const [width, setWidth] = useState(() => {
        const saved = localStorage.getItem("channels-sidebar-width");
        return saved ? Number(saved) : 320;
    });

    useEffect(() => {
        localStorage.setItem("channels-sidebar-width", String(width));
    }, [width]);

    useEffect(() => {
        const handleResize = () => setIsMobile(window.innerWidth < 768);
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    if(isLoading) {
        return <LoadingFallback />;
    }

    if(!emailVerified) {
        return <h1>UnAuthorized to view this page</h1>
    }

    return (
        <div className="flex h-full min-h-0">
            <div
                className={`${isMobile ? (id ? "hidden": "") : ""} min-w-0 shrink-0`}
                style={isMobile ? {width: "100%"} : {width}}
            >
                <ChannelAside isMobile={isMobile} width={width} setWidth={setWidth}/>
            </div>

            <div className={`flex-1 ${isMobile ? (!id ? 'hidden': ''): ''}`}>
                <Outlet />
            </div>
        </div>
    );
}

export default ChannelsPage;