// Utilidades de validación
export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

export const validatePassword = (password) => {
  return password && password.length >= 6;
};

export const validateRequired = (value) => {
  return value && value.trim().length > 0;
};

// Función para validar formulario de login
export const validateLoginForm = (formData) => {
  const errors = {};

  if (!validateRequired(formData.email)) {
    errors.email = 'El email es requerido';
  } else if (!validateEmail(formData.email)) {
    errors.email = 'El email no es válido';
  }

  if (!validateRequired(formData.password)) {
    errors.password = 'La contraseña es requerida';
  } else if (!validatePassword(formData.password)) {
    errors.password = 'La contraseña debe tener al menos 6 caracteres';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Función para validar formulario de registro
export const validateRegisterForm = (formData) => {
  const errors = {};

  if (!validateRequired(formData.email)) {
    errors.email = 'El email es requerido';
  } else if (!validateEmail(formData.email)) {
    errors.email = 'El email no es válido';
  }

  if (!validateRequired(formData.password)) {
    errors.password = 'La contraseña es requerida';
  } else if (!validatePassword(formData.password)) {
    errors.password = 'La contraseña debe tener al menos 6 caracteres';
  }

  if (!validateRequired(formData.confirmPassword)) {
    errors.confirmPassword = 'Confirma tu contraseña';
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Función para formatear errores de API
export const formatApiError = (error) => {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.message) {
    return error.message;
  }
  return 'Ha ocurrido un error inesperado';
};
