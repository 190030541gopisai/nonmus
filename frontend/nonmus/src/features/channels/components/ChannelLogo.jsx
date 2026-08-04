import {useState} from "react";

const SIZES = {
    sm: "h-8 w-8 text-sm",
    md: "h-12 w-12 text-lg",
    lg: "h-20 w-20 text-3xl",
    xl: "h-28 w-28 text-5xl",
};

function ChannelLogo({channel, size = "md"}) {
    const [imageError, setImageError] = useState(false);

    const sizeClass = SIZES[size] || SIZES.md;

    if (channel.logo && !imageError) {
        return (
            <img
                src={channel.logo}
                alt={`${channel.name} logo`}
                referrerPolicy="no-referrer"
                onError={() => setImageError(true)}
                className={`${sizeClass} rounded-full object-cover ring-2 ring-gray-100 transition-transform duration-200 group-hover:scale-105 cursor-pointer`}
            />
        );
    }

    return (
        <div
            className={`${sizeClass} flex shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 font-bold text-white ring-2 ring-gray-100 transition-transform duration-200 group-hover:scale-105 cursor-pointer`}
        >
            {channel.name?.charAt(0)?.toUpperCase() || "C"}
        </div>
    );
}

export default ChannelLogo;
