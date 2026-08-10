import ChannelLogo from "./ChannelLogo.jsx";

function Channel({channel, index, className = "", ...rest}) {
    return <div
        key={channel.channelId || index}
        className={`w-full min-w-0 overflow-hidden ${className}`}
        {...rest}
        >
        <ChannelLogo channel={channel}/>

        <div className="min-w-0 flex-1">
            <h1 className="truncate">
                {channel.name || "Channel Name"}
            </h1>
            <p className="truncate">
                {channel.subscribersCount + " " + (channel.subscribersCount === 1 ? "member" : "members")}
            </p>
        </div>
    </div>
}

export default Channel;