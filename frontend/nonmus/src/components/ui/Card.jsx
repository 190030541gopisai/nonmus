import React from "react";
import PropTypes from "prop-types";

const Card = ({
  children,
  className = "",
  header,
  footer,
  ...props
}) => {
  return (
    <div className={`bg-white rounded-lg shadow-md overflow-hidden ${className}`} {...props}>
      {header && <div className="p-6 border-b">{header}</div>}
      <div className="p-6">{children}</div>
      {footer && <div className="p-6 border-t">{footer}</div>}
    </div>
  );
};

Card.propTypes = {
  children: PropTypes.node.isRequired,
  className: PropTypes.string,
  header: PropTypes.oneOfType([PropTypes.node, PropTypes.string]),
  footer: PropTypes.oneOfType([PropTypes.node, PropTypes.string]),
};

Card.defaultProps = {
  className: "",
  header: null,
  footer: null,
};

export default Card;