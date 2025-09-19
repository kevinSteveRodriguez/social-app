import React, { useState } from 'react';
import { AuthLayout } from '../layout';
import { Button, Input, Alert } from '../ui';
import { useForm } from '../../hooks';
import { validateRegisterForm } from '../../utils';
import { APP_CONFIG } from '../../constants';
import { authService } from '../../services';

const Register = ({ onSwitchToLogin }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  
  const {
    values,
    errors,
    touched,
    handleChange,
    handleBlur,
    validate,
    reset
  } = useForm(
    { email: '', password: '', confirmPassword: '' },
    validateRegisterForm
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validate()) {
      return;
    }

    setIsLoading(true);
    setApiError('');
    setSuccessMessage('');
    
    try {
      const response = await authService.register({
        email: values.email,
        password: values.password
      });
      
      if (response.success) {
        setSuccessMessage('¡Registro exitoso! Ya puedes iniciar sesión.');
        
        reset();
        
        setTimeout(() => {
          if (onSwitchToLogin) {
            onSwitchToLogin();
          }
        }, 2000);
      }
      
    } catch (error) {
      setApiError(error.message || 'Error en el registro. Inténtalo de nuevo.');
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
            Crear Cuenta
          </h1>
          <p className="text-gray-600">
            Únete a {APP_CONFIG.name}
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

          {/* Mensaje de éxito */}
          <Alert 
            type="success" 
            message={successMessage} 
            onClose={() => setSuccessMessage('')}
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

          <Input
            label="Confirmar Contraseña"
            type="password"
            name="confirmPassword"
            value={values.confirmPassword}
            onChange={handleChange}
            onBlur={handleBlur}
            placeholder="Confirma tu contraseña"
            error={errors.confirmPassword}
            touched={touched.confirmPassword}
            disabled={isLoading}
            required
          />

          <Button
            type="submit"
            className="w-full"
            loading={isLoading}
            disabled={isLoading}
          >
            {isLoading ? 'Creando cuenta...' : 'Crear Cuenta'}
          </Button>
        </form>

        {/* Footer */}
        <div className="mt-8 text-center">
          <p className="text-sm text-gray-600">
            ¿Ya tienes cuenta?{' '}
            <button 
              type="button"
              onClick={onSwitchToLogin}
              className="text-primary-600 hover:text-primary-700 font-medium transition-colors"
            >
              Inicia sesión aquí
            </button>
          </p>
        </div>
      </div>
    </AuthLayout>
  );
};

export default Register;
