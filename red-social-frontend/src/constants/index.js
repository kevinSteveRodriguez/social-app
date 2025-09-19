// Constantes de la aplicación
export const APP_CONFIG = {
  name: 'Mi Red Social',
  version: '1.0.0',
  description: 'Conecta con tus amigos',
};

// Rutas de la aplicación
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  PROFILE: '/profile',
  SETTINGS: '/settings',
};

// Configuración de la API
export const API_CONFIG = {
  BASE_URL: '/api', // Usar ruta relativa para el proxy
  TIMEOUT: 10000,
};

// Mensajes de validación
export const VALIDATION_MESSAGES = {
  REQUIRED: 'Este campo es requerido',
  EMAIL_INVALID: 'El email no es válido',
  PASSWORD_MIN_LENGTH: 'La contraseña debe tener al menos 6 caracteres',
  PASSWORD_MISMATCH: 'Las contraseñas no coinciden',
};

// Estados de carga
export const LOADING_STATES = {
  IDLE: 'idle',
  LOADING: 'loading',
  SUCCESS: 'success',
  ERROR: 'error',
};
