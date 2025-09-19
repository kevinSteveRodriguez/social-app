import { API_CONFIG } from '../constants';

class PostsService {
  constructor() {
    this.baseURL = API_CONFIG.BASE_URL;
  }

  async getPosts(page = 0, size = 10) {
    try {
      const response = await fetch(`${this.baseURL}/posts?page=${page}&size=${size}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getToken()}`
        }
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      return {
        success: true,
        data: data.content || [],
        pagination: {
          pageNumber: data.pageable?.pageNumber || 0,
          pageSize: data.pageable?.pageSize || 10,
          totalPages: data.totalPages || 0,
          totalElements: data.totalElements || 0,
          last: data.last || true,
          first: data.first || true
        }
      };
    } catch (error) {
      throw error;
    }
  }

  async createPost(content, mediaUrl = null) {
    try {
      const response = await fetch(`${this.baseURL}/posts`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.getToken()}`
        },
        body: JSON.stringify({
          content: content,
          mediaUrl: mediaUrl
        })
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

export default new PostsService();
