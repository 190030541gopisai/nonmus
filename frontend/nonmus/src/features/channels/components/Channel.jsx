import ChannelLogo from "./ChannelLogo.jsx";

function Channel({channel, index}) {
    return <div
        key={channel.channelId || index}
        id="channel"
        className="group flex items-center gap-4 p-2 pl-4 border border-gray-200 bg-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow-lg hover:border-gray-300"
    >
        <ChannelLogo channel={channel}/>

        <div className="min-w-0 flex-1">
            <h1 className="truncate font-semibold text-gray-900">
                {channel.name || "Channel Name"}
            </h1>
            <p className="truncate text-gray-500">
                {channel.subscribersCount + " " + (channel.subscribersCount === 1 ? "member" : "members")}
            </p>
        </div>
    </div>
}

export default Channel;