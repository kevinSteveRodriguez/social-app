import { API_CONFIG } from '../constants';
import authService from './authService';

class ProfileService {
  constructor() {
    this.baseURL = API_CONFIG.BASE_URL;
  }

  async getUserIdFromEmail() {
    try {
      const token = this.getToken();
      if (!token) {
        throw new Error('No hay token disponible');
      }

      const decoded = authService.decodeToken(token);
      const email = decoded ? decoded.sub : null;
      
      if (!email) {
        throw new Error('No se pudo obtener el email del token');
      }

      const response = await fetch(`${this.baseURL}/user-profiles`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();

      let userId = null;
      if (Array.isArray(data)) {
        const userProfile = data.find(profile => profile.email === email);
        userId = userProfile ? userProfile.userId : null;
      } else if (data.email === email) {
        userId = data.userId;
      }

      return userId;

    } catch (error) {
      throw error;
    }
  }

  async getCurrentUserProfile() {
    try {
      let userId = authService.getCurrentUserId();

      if (!userId || !userId.includes('-')) {
        userId = await this.getUserIdFromEmail();
      }

      if (!userId) {
        throw new Error('No se pudo obtener el userId del usuario');
      }
      
      return await this.getUserProfile(userId);
    } catch (error) {
      throw error;
    }
  }

  async getUserProfile(userId) {
    try {
      const url = `${this.baseURL}/user-profiles/by-user/${userId}`;
      const token = this.getToken();

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        let errorData;
        try {
          errorData = await response.json();
        } catch (jsonError) {
          const textError = await response.text();
          errorData = { message: textError || `Error ${response.status}: ${response.statusText}` };
        }
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      return {
        success: true,
        data: data
      };
    } catch (error) {
      throw error;
    }
  }

  async updateUserProfile(userId, profileData) {
    try {
      const response = await fetch(`${this.baseURL}/user-profiles/by-user/${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getToken()}`
        },
        body: JSON.stringify(profileData)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      return {
        success: true,
        data: data
      };
    } catch (error) {
      throw error;
    }
  }

  getToken() {
    return localStorage.getItem('authToken');
  }
}

export default new ProfileService();
