import React, { useState } from 'react';
import { Button } from '../ui';
import { Alert } from '../ui';
import { postsService } from '../../services';

const CreatePost = ({ user, onPostCreated }) => {
  const [content, setContent] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) {
      setError('El contenido de la publicación no puede estar vacío');
      return;
    }

    setIsLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await postsService.createPost(content);
      
      if (response.success) {
        setSuccess('¡Publicación creada exitosamente!');
        setContent('');
        
        if (onPostCreated) {
          onPostCreated();
        }
      }
      
    } catch (error) {
      setError(error.message || 'Error al crear la publicación. Inténtalo de nuevo.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleContentChange = (e) => {
    setContent(e.target.value);
    if (error) setError('');
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <div className="flex items-start space-x-4">
        {/* Avatar del usuario */}
        <div className="w-12 h-12 bg-primary-600 rounded-full flex items-center justify-center flex-shrink-0">
          <span className="text-white font-medium">
            {user.email ? user.email.charAt(0).toUpperCase() : 'U'}
          </span>
        </div>

        {/* Formulario de publicación */}
        <div className="flex-1">
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Alertas */}
            <Alert 
              type="error" 
              message={error} 
              onClose={() => setError('')}
            />
            <Alert 
              type="success" 
              message={success} 
              onClose={() => setSuccess('')}
            />

            {/* Campo de texto */}
            <div>
              <textarea
                value={content}
                onChange={handleContentChange}
                placeholder="¿Qué estás pensando?"
                rows={4}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent resize-none"
                disabled={isLoading}
              />
            </div>

            {/* Contador de caracteres */}
            <div className="flex justify-between items-center">
              <span className={`text-sm ${content.length > 500 ? 'text-red-500' : 'text-gray-500'}`}>
                {content.length}/500 caracteres
              </span>
              
              <Button
                type="submit"
                disabled={isLoading || !content.trim() || content.length > 500}
                loading={isLoading}
                className="px-6"
              >
                {isLoading ? 'Publicando...' : 'Publicar'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreatePost;
