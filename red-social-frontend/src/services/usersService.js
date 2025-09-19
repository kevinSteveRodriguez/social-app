import { API_CONFIG } from '../constants';

// Servicio para manejar información de usuarios
class UsersService {
  constructor() {
    this.baseURL = API_CONFIG.BASE_URL;
  }

  // Obtener información de un usuario por ID
  async getUserById(userId) {
    try {
      const response = await fetch(`${this.baseURL}/users/${userId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getToken()}`
        }
      });

      if (!response.ok) {
        // Si no se puede obtener el usuario, devolver datos por defecto
        return {
          success: true,
          data: {
            id: userId,
            email: 'usuario@ejemplo.com',
            name: 'Usuario'
          }
        };
      }

      const data = await response.json();
      return {
        success: true,
        data: data
      };
    } catch (error) {
      console.error('Error al obtener usuario:', error);
      // Devolver datos por defecto en caso de error
      return {
        success: true,
        data: {
          id: userId,
          email: 'usuario@ejemplo.com',
          name: 'Usuario'
        }
      };
    }
  }

  // Obtener token del localStorage
  getToken() {
    return localStorage.getItem('authToken');
  }
}

export default new UsersService();
