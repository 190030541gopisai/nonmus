import React from "react";

const SkeletonLoader = ({
  type = "text",
  className = "",
  width = "100%",
  height = "1rem",
  borderRadius = "0.25rem"
}) => {
  return (
    <div
      className={`animate-pulse bg-gray-200 rounded ${className}`}
      style={{ width, height, borderRadius }}
    ></div>
  );
};

export default SkeletonLoader;