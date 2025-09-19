import React from 'react';

const Input = ({ 
  label, 
  error, 
  touched,
  className = '',
  ...props 
}) => {
  const inputClasses = `
    w-full px-4 py-3 border rounded-lg transition-all duration-200 
    focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
    ${error && touched 
      ? 'border-red-500 bg-red-50' 
      : 'border-gray-300 bg-white hover:border-gray-400'
    }
    ${props.disabled ? 'bg-gray-100 cursor-not-allowed opacity-70' : ''}
    ${className}
  `.trim();

  return (
    <div className="space-y-2">
      {label && (
        <label className="block text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <input
        className={inputClasses}
        {...props}
      />
      {error && touched && (
        <p className="text-sm text-red-600 font-medium">{error}</p>
      )}
    </div>
  );
};

export default Input;
