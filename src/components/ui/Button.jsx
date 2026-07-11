import React from "react";
import PropTypes from "prop-types";

const Button = ({
  children,
  variant = "primary",
  size = "medium",
  type = "button",
  disabled = false,
  onClick,
  className = "",
  ...props
}) => {
  const baseClasses = "transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-offset-2";

  const variantClasses = {
    primary: "bg-primary-600 text-white hover:bg-primary-700",
    secondary: "bg-gray-200 text-gray-800 hover:bg-gray-300",
    danger: "bg-red-600 text-white hover:bg-red-700",
    outline: "border border-gray-300 text-gray-700 hover:bg-gray-50",
    ghost: "text-gray-700 hover:bg-gray-50",
  }[variant] || variant;

  const sizeClasses = {
    small: "px-3 py-1.5 text-sm",
    medium: "px-4 py-2 text-base",
    large: "px-6 py-3 text-lg",
  }[size] || size;

  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      className={`${baseClasses} ${variantClasses} ${sizeClasses} rounded-md ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};

Button.propTypes = {
  children: PropTypes.node.isRequired,
  variant: PropTypes.oneOf(["primary", "secondary", "danger", "outline", "ghost"]),
  size: PropTypes.oneOf(["small", "medium", "large"]),
  type: PropTypes.oneOf(["button", "submit", "reset"]),
  disabled: PropTypes.bool,
  onClick: PropTypes.func,
  className: PropTypes.string,
};

Button.defaultProps = {
  variant: "primary",
  size: "medium",
  type: "button",
  disabled: false,
  onClick: undefined,
  className: "",
};

export default Button;