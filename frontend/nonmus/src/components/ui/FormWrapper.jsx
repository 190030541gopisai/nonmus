import React from "react";
import PropTypes from "prop-types";

const FormWrapper = ({
  children,
  onSubmit,
  loading = false,
  error = null,
  success = null,
  resetOnSuccess = true,
  ...props
}) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSubmit) {
      onSubmit(e);
    }
  };

  return (
    <form onSubmit={handleSubmit} {...props}>
      <div className="space-y-6">
        {children}
        {error && (
          <div className="p-4 mb-4 bg-red-50 border border-red-200 rounded-md">
            <p className="text-sm text-red-800">{error}</p>
          </div>
        )}
        {success && (
          <div className="p-4 mb-4 bg-green-50 border border-green-200 rounded-md">
            <p className="text-sm text-green-800">{success}</p>
          </div>
        )}
        <button
          type="submit"
          disabled={loading}
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
          {loading ? "Submitting..." : "Submit"}
        </button>
      </div>
    </form>
  );
};

FormWrapper.propTypes = {
  children: PropTypes.node.isRequired,
  onSubmit: PropTypes.func,
  loading: PropTypes.bool,
  error: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
  success: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
  resetOnSuccess: PropTypes.bool,
};

FormWrapper.defaultProps = {
  onSubmit: undefined,
  loading: false,
  error: null,
  success: null,
  resetOnSuccess: true,
};

export default FormWrapper;