import React, { useState, useEffect } from 'react';
import { Button } from '../ui';
import { profileService, authService } from '../../services';

const Profile = ({ user }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [profileData, setProfileData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    alias: '',
    bio: '',
    birthDate: '',
    avatarUrl: ''
  });

  useEffect(() => {
    const loadProfile = async () => {
      setIsLoading(true);
      setError('');
      
        try {
          const response = await profileService.getCurrentUserProfile();
        
        if (response.success) {
          const profile = response.data;
          setProfileData({
            firstName: profile.firstName || '',
            lastName: profile.lastName || '',
            email: profile.email || '',
            alias: profile.alias || '',
            bio: profile.bio || '',
            birthDate: profile.birthDate || '',
            avatarUrl: profile.avatarUrl || ''
          });
        }
        } catch (error) {
          setError('Error al cargar el perfil');
        } finally {
          setIsLoading(false);
        }
    };

    loadProfile();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProfileData(prev => ({
      ...prev,
      [name]: value
    }));
  };

        const handleSave = async () => {
          try {
            let userId = authService.getCurrentUserId();
            
            if (!userId || !userId.includes('-')) {
              userId = await profileService.getUserIdFromEmail();
            }

            if (!userId) {
              throw new Error('No se pudo obtener el ID del usuario');
            }
            
            const response = await profileService.updateUserProfile(userId, profileData);
      
      if (response.success) {
        setIsEditing(false);
        alert('Perfil actualizado exitosamente');
      }
          } catch (error) {
            alert('Error al guardar el perfil: ' + error.message);
          }
        };

        const handleCancel = () => {
          const loadOriginalData = async () => {
            try {
              const response = await profileService.getCurrentUserProfile();
              if (response.success) {
                const profile = response.data;
                setProfileData({
                  firstName: profile.firstName || '',
                  lastName: profile.lastName || '',
                  email: profile.email || '',
                  alias: profile.alias || '',
                  bio: profile.bio || '',
                  birthDate: profile.birthDate || '',
                  avatarUrl: profile.avatarUrl || ''
                });
              }
            } catch (error) {
              // Error silencioso al cancelar
            }
          };
          
          loadOriginalData();
          setIsEditing(false);
        };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-sm border p-8">
          <div className="animate-pulse">
            <div className="flex items-center space-x-6">
              <div className="w-24 h-24 bg-gray-300 rounded-full"></div>
              <div className="flex-1 space-y-3">
                <div className="h-8 bg-gray-300 rounded w-1/3"></div>
                <div className="h-4 bg-gray-300 rounded w-1/4"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-sm border p-8 text-center">
          <div className="text-red-500">
            <svg className="mx-auto h-12 w-12 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h3 className="mt-2 text-sm font-medium text-gray-900">Error al cargar perfil</h3>
            <p className="mt-1 text-sm text-gray-500">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-lg shadow-sm border">
        {/* Header del Perfil */}
        <div className="px-6 py-8 border-b">
          <div className="flex items-center space-x-6">
            {profileData.avatarUrl ? (
              <img 
                src={profileData.avatarUrl} 
                alt="Avatar" 
                className="w-24 h-24 rounded-full object-cover"
              />
            ) : (
              <div className="w-24 h-24 bg-primary-600 rounded-full flex items-center justify-center">
                <span className="text-white text-3xl font-bold">
                  {profileData.firstName ? profileData.firstName.charAt(0).toUpperCase() : 
                   profileData.alias ? profileData.alias.charAt(0).toUpperCase() : 'U'}
                </span>
              </div>
            )}
            
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900">
                {profileData.firstName && profileData.lastName 
                  ? `${profileData.firstName} ${profileData.lastName}`
                  : profileData.alias || 'Usuario'}
              </h1>
              <p className="text-gray-600 mt-1">@{profileData.alias}</p>
              <p className="text-gray-500 mt-1">{profileData.email}</p>
            </div>
            
            <div>
              <Button
                onClick={() => setIsEditing(!isEditing)}
                variant={isEditing ? "outline" : "primary"}
              >
                {isEditing ? 'Cancelar' : 'Editar Perfil'}
              </Button>
            </div>
          </div>
        </div>

        {/* Contenido del Perfil */}
        <div className="px-6 py-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Información Personal */}
            <div className="space-y-6">
              <h2 className="text-xl font-semibold text-gray-900">Información Personal</h2>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Nombre
                  </label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="firstName"
                      value={profileData.firstName}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      placeholder="Tu nombre"
                    />
                  ) : (
                    <p className="text-gray-900">{profileData.firstName || 'No especificado'}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Apellido
                  </label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="lastName"
                      value={profileData.lastName}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      placeholder="Tu apellido"
                    />
                  ) : (
                    <p className="text-gray-900">{profileData.lastName || 'No especificado'}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Alias
                  </label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="alias"
                      value={profileData.alias}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      placeholder="Tu alias"
                    />
                  ) : (
                    <p className="text-gray-900">@{profileData.alias || 'No especificado'}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email
                  </label>
                  <p className="text-gray-900">{profileData.email}</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Fecha de Nacimiento
                  </label>
                  {isEditing ? (
                    <input
                      type="date"
                      name="birthDate"
                      value={profileData.birthDate}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    />
                  ) : (
                    <p className="text-gray-900">
                      {profileData.birthDate 
                        ? new Date(profileData.birthDate).toLocaleDateString('es-ES')
                        : 'No especificada'
                      }
                    </p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    URL del Avatar
                  </label>
                  {isEditing ? (
                    <input
                      type="url"
                      name="avatarUrl"
                      value={profileData.avatarUrl}
                      onChange={handleInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      placeholder="https://ejemplo.com/avatar.jpg"
                    />
                  ) : (
                    <p className="text-gray-900">
                      {profileData.avatarUrl ? (
                        <a href={profileData.avatarUrl} target="_blank" rel="noopener noreferrer" className="text-primary-600 hover:underline">
                          Ver avatar
                        </a>
                      ) : (
                        'No especificado'
                      )}
                    </p>
                  )}
                </div>
              </div>
            </div>

            {/* Biografía */}
            <div className="space-y-6">
              <h2 className="text-xl font-semibold text-gray-900">Biografía</h2>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Sobre ti
                </label>
                {isEditing ? (
                  <textarea
                    name="bio"
                    value={profileData.bio}
                    onChange={handleInputChange}
                    rows={6}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                    placeholder="Cuéntanos algo sobre ti..."
                  />
                ) : (
                  <p className="text-gray-900 whitespace-pre-wrap">
                    {profileData.bio || 'No hay biografía disponible'}
                  </p>
                )}
              </div>
            </div>
          </div>

          {/* Botones de Acción */}
          {isEditing && (
            <div className="mt-8 flex justify-end space-x-4">
              <Button
                onClick={handleCancel}
                variant="outline"
              >
                Cancelar
              </Button>
              <Button
                onClick={handleSave}
                variant="primary"
              >
                Guardar Cambios
              </Button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;
