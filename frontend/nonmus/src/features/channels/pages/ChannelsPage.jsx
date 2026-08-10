import {Outlet, useParams} from "react-router-dom";
import ChannelAside from "../components/ChannelAside.jsx";

function ChannelsPage() {
    const {id} = useParams();

    return (
        <div className="flex h-full flex-col md:flex-row">
            <div className={id ? "hidden md:block" : "md:block"}>
                <ChannelAside />
            </div>
            <div className={`flex-1`}>
                <Outlet />
            </div>
        </div>
    );
}

export default ChannelsPage;
