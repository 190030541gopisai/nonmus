import {useEffect, useRef, useState} from "react";

const ROW_HEIGHT = 40;
const PAGE_SIZE = 20;
const OVERSCAN = 10;

function Demo() {
    const [lists, setLists] = useState(
        Array.from({ length: 20 }, (_, index) => index + 1)
    );
    const containerRef = useRef();

    const [viewportHeight, setViewportHeight] = useState(64);

    const [scrollTop, setScrollTop] = useState(0);

    // Which row should we start rendering?
    const startIndex = Math.max(
        0,
        Math.floor(scrollTop / ROW_HEIGHT) - OVERSCAN
    );

    // Which row should we stop rendering?
    const endIndex = Math.min(
        lists.length,
        Math.ceil(
            (scrollTop + viewportHeight) / ROW_HEIGHT
        ) + OVERSCAN
    );

    // Only take the rows we actually need
    const visibleItems = lists.slice(
        startIndex,
        endIndex
    );

    console.log("Start = " + startIndex);
    console.log("End = " + endIndex);
    console.log("Viewport = " + viewportHeight);

    // Fake space for rows above
    const topHeight =
        startIndex * ROW_HEIGHT;

    // Fake space for rows below
    const bottomHeight =
        (lists.length - endIndex) * ROW_HEIGHT;


    const handleScroll = (e) => {
        const top = e.currentTarget.scrollTop;
        setScrollTop(top);

        const clientHeight = e.currentTarget.clientHeight;

        const contentHeight = lists.length * ROW_HEIGHT;



        // console.log("scrollTop:", top);
        // console.log("clientHeight:", clientHeight);
        // console.log("contentHeight:", contentHeight);

        if (top + clientHeight >= contentHeight - 100) {
            setLists((prev) => [
                ...prev,
                ...Array.from(
                    { length: PAGE_SIZE },
                    (_, index) => prev.length + index + 1
                )
            ]);
        }
    };

    useEffect(() => {
        const el = containerRef.current;
        if(!el) return;
        const observer = new ResizeObserver(() => {
            setViewportHeight(el.clientHeight);
        });
        observer.observe(el);
        return () => observer.disconnect();

    }, []);

    return (
        <div
            className="h-[200px] overflow-y-auto border"
            onScroll={handleScroll}
            ref={containerRef}
        >
            {/* SPACE FOR ITEMS ABOVE */}
            <div
                style={{
                    height: topHeight
                }}
            />

            <ul>
                {visibleItems.map((item) => (
                    <li
                        key={item}
                        className="h-10 border-b"
                    >
                        Item {item}
                    </li>
                ))}
            </ul>

            {/* SPACE FOR ITEMS BELOW */}
            <div
                style={{
                    height: bottomHeight
                }}
            />
        </div>
    );
}

export default Demo;