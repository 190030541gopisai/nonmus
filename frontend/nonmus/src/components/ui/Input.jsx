import React, { memo } from "react";
import PropTypes from "prop-types";

const Input = ({
  type = "text",
  label,
  placeholder = "",
  value = "",
  onChange,
  error = false,
  helperText = "",
  disabled = false,
  className = "",
  ...props
}) => {
  return (
    <div className="mb-4">
      {label && (
        <label
          htmlProps={{ htmlFor: label.toLowerCase().replace(/\s+/g, "-") }}
          className="mb-2 block text-sm font-medium text-gray-700"
        >
          {label}
        </label>
      )}
      <div className="relative">
        <input
          type={type}
          id={label ? label.toLowerCase().replace(/\s+/g, "-") : undefined}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          disabled={disabled}
          className={`block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 ${error ? "ring-red-300" : ""} ${className}`}
          {...props}
        />
        {error && helperText ? (
          <p className="mt-1 text-sm text-red-600">{helperText}</p>
        ) : !error && helperText ? (
          <p className="mt-1 text-sm text-gray-500">{helperText}</p>
        ) : null}
      </div>
    </div>
  );
};

Input.propTypes = {
  type: PropTypes.string,
  label: PropTypes.string,
  placeholder: PropTypes.string,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  error: PropTypes.bool,
  helperText: PropTypes.string,
  disabled: PropTypes.bool,
  className: PropTypes.string,
};

Input.defaultProps = {
  type: "text",
  label: "",
  placeholder: "",
  value: "",
  onChange: () => {},
  error: false,
  helperText: "",
  disabled: false,
  className: "",
};

export default memo(Input);