import ChannelAside from "../components/ChannelAside.jsx";
import ChannelContent from "../components/ChannelContent.jsx";

function ChannelsPage() {

    return (
        <div className="fixed">
            <div className="flex">
                <ChannelAside />
                <ChannelContent />
            </div>
        </div>
    );
}

export default ChannelsPage;
