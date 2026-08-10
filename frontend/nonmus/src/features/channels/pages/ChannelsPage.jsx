import {useEffect, useRef, useState} from "react";
import {Outlet, useParams} from "react-router-dom";
import ChannelAside from "../components/ChannelAside.jsx";

function ChannelsPage() {
    const {id} = useParams();

    const contentRef = useRef(null);

    const [isMobile, setIsMobile] = useState(() => window.innerWidth < 768);

    useEffect(() => {
        const handleResize = () => setIsMobile(window.innerWidth < 768);
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    const [width, setWidth] = useState(() => {
        const saved = localStorage.getItem("channels-sidebar-width");
        return saved ? Number(saved) : 320;
    });

    useEffect(() => {
        localStorage.setItem("channels-sidebar-width", String(width));
    }, [width]);

    // Reset the content pane to the top whenever the selected channel changes,
    // otherwise the reused DOM keeps its previous scroll position.
    useEffect(() => {
        contentRef.current?.scrollTo(0, 0);
    }, [id]);

    return (
        <div className="flex h-full min-h-0">
            <div
                className={`${isMobile ? (id
                            ? "hidden"
                            : "") : ""} min-w-0 shrink-0`}
                style={isMobile ? {width: "100%"} : {width}}
            >
                <ChannelAside isMobile={isMobile} width={width} setWidth={setWidth}/>
            </div>

            <div ref={contentRef} className={`flex-1 ${isMobile ? (!id ? 'hidden': ''): ''}`}>
                <Outlet />
            </div>
        </div>
    );
}

export default ChannelsPage;