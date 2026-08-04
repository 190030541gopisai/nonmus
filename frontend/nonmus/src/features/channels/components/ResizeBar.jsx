function ResizeBar({width, setWidth}) {
    const handleMouseDown = (e) => {
        e.preventDefault();

        const startX = e.clientX;
        const startWidth = width;

        const handleMouseMove = (e) => {
            const newWidth = startWidth + (e.clientX - startX);

            // Limit width
            setWidth(Math.min(Math.max(newWidth, 250), 600));
        };

        const handleMouseUp = () => {
            document.removeEventListener("mousemove", handleMouseMove);
            document.removeEventListener("mouseup", handleMouseUp);
        };

        document.addEventListener("mousemove", handleMouseMove);
        document.addEventListener("mouseup", handleMouseUp);
    };

    return <div
        onMouseDown={handleMouseDown}
        className="hidden md:block h-full w-4 cursor-col-resize absolute top-0 -right-2"
    />;
}

export default ResizeBar;