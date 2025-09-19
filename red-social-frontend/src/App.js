import React, { useState, useEffect } from 'react';
import { Login, Register } from './components/auth';
import { Dashboard } from './components/dashboard';
import { authService } from './services';

function App() {
  const [currentView, setCurrentView] = useState('login');
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Verificar si el usuario está autenticado al cargar la app
    const checkAuth = () => {
      if (authService.isAuthenticated() && !authService.isTokenExpired()) {
        setIsAuthenticated(true);
        setCurrentView('dashboard');
      } else {
        // Limpiar datos si el token está expirado
        authService.logout();
        setIsAuthenticated(false);
        setCurrentView('login');
      }
    };

    checkAuth();
  }, []);

  const handleSwitchToRegister = () => {
    setCurrentView('register');
  };

  const handleSwitchToLogin = () => {
    setCurrentView('login');
  };

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    setCurrentView('dashboard');
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setCurrentView('login');
  };

  // Si está autenticado, mostrar el dashboard
  if (isAuthenticated) {
    return <Dashboard onLogout={handleLogout} />;
  }

  // Si no está autenticado, mostrar login o register
  return (
    <div className="App">
      {currentView === 'login' ? (
        <Login 
          onSwitchToRegister={handleSwitchToRegister}
          onLoginSuccess={handleLoginSuccess}
        />
      ) : (
        <Register onSwitchToLogin={handleSwitchToLogin} />
      )}
    </div>
  );
}

export default App;
