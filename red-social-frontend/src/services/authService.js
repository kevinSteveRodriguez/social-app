import { API_CONFIG } from '../constants';

class AuthService {
  constructor() {
    this.baseURL = API_CONFIG.BASE_URL;
  }

  async login(credentials) {
    try {
      const response = await fetch(`${this.baseURL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: credentials.email,
          password: credentials.password
        })
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      
      localStorage.setItem('authToken', data.token);
      
      const user = {
        email: credentials.email,
        token: data.token
      };
      localStorage.setItem('user', JSON.stringify(user));
      
      return {
        success: true,
        data: {
          user,
          token: data.token
        }
      };
    } catch (error) {
      throw error;
    }
  }

  async register(credentials) {
    try {
      const response = await fetch(`${this.baseURL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: credentials.email,
          password: credentials.password
        })
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      
      return {
        success: true,
        data: {
          message: 'Usuario registrado exitosamente',
          email: credentials.email
        }
      };
    } catch (error) {
      throw error;
    }
  }

  async logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    return { success: true };
  }

  isAuthenticated() {
    const token = localStorage.getItem('authToken');
    return !!token;
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  getCurrentUserId() {
    const token = this.getToken();
    if (!token) return null;
    
    const decoded = this.decodeToken(token);
    const userId = decoded ? (decoded.userId || decoded.id || decoded.sub) : null;
    
    return userId;
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  decodeToken(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (error) {
      return null;
    }
  }

  isTokenExpired() {
    const token = this.getToken();
    if (!token) return true;
    
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return true;
    
    const currentTime = Date.now() / 1000;
    return decoded.exp < currentTime;
  }
}

export default new AuthService();
