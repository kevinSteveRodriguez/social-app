import React, { useState } from 'react';
import { AuthLayout } from '../layout';
import { Button, Input, Alert } from '../ui';
import { useForm } from '../../hooks';
import { validateLoginForm } from '../../utils';
import { APP_CONFIG } from '../../constants';
import { authService } from '../../services';

const Login = ({ onSwitchToRegister, onLoginSuccess }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  
  const {
    values,
    errors,
    touched,
    handleChange,
    handleBlur,
    validate,
    reset
  } = useForm(
    { email: '', password: '' },
    validateLoginForm
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validate()) {
      return;
    }

    setIsLoading(true);
    setApiError('');
    
          try {
            const response = await authService.login(values);
            
            if (response.success) {
              if (onLoginSuccess) {
                onLoginSuccess();
              }
              
              reset();
            }
            
          } catch (error) {
            setApiError(error.message || 'Error en el login. Inténtalo de nuevo.');
          } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout>
      <div className="bg-white rounded-2xl shadow-2xl p-8 animate-slide-up">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            {APP_CONFIG.name}
          </h1>
          <p className="text-gray-600">
            {APP_CONFIG.description}
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Error de API */}
          <Alert 
            type="error" 
            message={apiError} 
            onClose={() => setApiError('')}
          />

          <Input
            label="Email"
            type="email"
            name="email"
            value={values.email}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="tu@email.com"
            error={errors.email}
            touched={touched.email}
            disabled={isLoading}
            required
          />

          <Input
            label="Contraseña"
            type="password"
            name="password"
            value={values.password}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Tu contraseña"
            error={errors.password}
            touched={touched.password}
            disabled={isLoading}
            required
          />

          <Button
            type="submit"
            className="w-full"
            loading={isLoading}
            disabled={isLoading}
          >
            {isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
          </Button>
        </form>

        {/* Footer */}
        <div className="mt-8 text-center space-y-4">
          <p className="text-sm text-gray-600">
            ¿No tienes cuenta?{' '}
            <button 
              type="button"
              onClick={onSwitchToRegister}
              className="text-primary-600 hover:text-primary-700 font-medium transition-colors"
            >
              Regístrate aquí
            </button>
          </p>
        </div>
      </div>
    </AuthLayout>
  );
};

export default Login;
