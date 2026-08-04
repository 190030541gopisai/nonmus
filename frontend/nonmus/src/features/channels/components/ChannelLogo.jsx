import {useEffect, useState} from "react";

function ChannelLogo({channel}) {
    const [imageError, setImageError] = useState(false);

    if (channel.logo && !imageError) {
        return (
            <img
                src={channel.logo}
                alt={`${channel.name} logo`}
                referrerPolicy="no-referrer"
                onError={() => setImageError(true)}
                className="h-12 w-12 rounded-full object-cover ring-2 ring-gray-100 transition-transform duration-200 group-hover:scale-105 cursor-pointer"
            />
        );
    }

    return (
        <div
            className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 text-lg font-bold text-white ring-2 ring-gray-100 transition-transform duration-200 group-hover:scale-105 cursor-pointer"
        >
            {channel.name?.charAt(0)?.toUpperCase() || "C"}
        </div>
    );
}

export default ChannelLogo;