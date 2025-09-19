package com.redsocial.app.service;

import com.redsocial.app.dto.UserProfileResponse;
import com.redsocial.app.exception.ResourceNotFoundException;
import com.redsocial.app.exception.UserProfileException;
import com.redsocial.app.exception.ValidationException;
import com.redsocial.app.model.UserProfile;
import com.redsocial.app.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> listAll() {
        logger.debug("Listando todos los perfiles de usuario");
        
        try {
            List<UserProfileResponse> profiles = repository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            
            logger.info("Se encontraron {} perfiles de usuario", profiles.size());
            return profiles;
            
        } catch (Exception e) {
            logger.error("Error al listar perfiles de usuario: ", e);
            throw new UserProfileException("Error interno al listar perfiles de usuario", e);
        }
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getById(UUID id) {
        logger.debug("Buscando perfil de usuario por ID: {}", id);
        
        try {
            validateId(id);
            
            UserProfile profile = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de usuario no encontrado con ID: " + id));
            
            logger.info("Perfil de usuario encontrado con ID: {}", id);
            return convertToDto(profile);
            
        } catch (ValidationException | ResourceNotFoundException e) {
            logger.warn("Error al buscar perfil por ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al buscar perfil por ID {}: ", id, e);
            throw new UserProfileException("Error interno al buscar perfil de usuario", e);
        }
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getByUserId(UUID userId) {
        logger.debug("Buscando perfil de usuario por User ID: {}", userId);
        
        try {
            validateUserId(userId);
            
            UserProfile profile = repository.findByUser_Id(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil de usuario no encontrado para userId: " + userId));
            
            logger.info("Perfil de usuario encontrado para User ID: {}", userId);
            return convertToDto(profile);
            
        } catch (ValidationException | ResourceNotFoundException e) {
            logger.warn("Error al buscar perfil por User ID {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al buscar perfil por User ID {}: ", userId, e);
            throw new UserProfileException("Error interno al buscar perfil de usuario por userId", e);
        }
    }

    private UserProfileResponse convertToDto(UserProfile profile) {
        try {
            if (profile == null) {
                throw new UserProfileException("El perfil de usuario no puede ser nulo");
            }
            
            if (profile.getUser() == null) {
                throw new UserProfileException("El usuario asociado al perfil no puede ser nulo");
            }
            
            return new UserProfileResponse(
                    profile.getId(),
                    profile.getUser().getId(),
                    profile.getUser().getEmail(),
                    profile.getFirstName(),
                    profile.getLastName(),
                    profile.getAlias(),
                    profile.getBirthDate(),
                    profile.getBio(),
                    profile.getAvatarUrl(),
                    profile.getCreatedAt(),
                    profile.getUpdatedAt()
            );
        } catch (Exception e) {
            logger.error("Error al convertir perfil a DTO: ", e);
            throw new UserProfileException("Error al convertir perfil de usuario a DTO", e);
        }
    }

    /**
     * Valida que el ID no sea nulo.
     */
    private void validateId(UUID id) {
        if (id == null) {
            throw new ValidationException("El ID no puede ser nulo");
        }
    }

    /**
     * Valida que el ID del usuario no sea nulo.
     */
    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new ValidationException("El ID del usuario no puede ser nulo");
        }
    }
}
