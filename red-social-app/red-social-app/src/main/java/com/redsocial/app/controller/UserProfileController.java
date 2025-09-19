package com.redsocial.app.controller;

import com.redsocial.app.dto.UserProfileResponse;
import com.redsocial.app.exception.ResourceNotFoundException;
import com.redsocial.app.exception.UserProfileException;
import com.redsocial.app.exception.ValidationException;
import com.redsocial.app.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-profiles")
@Tag(name = "Perfiles de Usuario", description = "Endpoints para gestión de perfiles de usuario")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los perfiles de usuario",
            description = "Obtiene una lista de todos los perfiles de usuario registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de perfiles obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class),
                            examples = @ExampleObject(
                                    name = "Lista de perfiles",
                                    value = "[{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"456e7890-e89b-12d3-a456-426614174001\", \"email\": \"usuario@ejemplo.com\", \"firstName\": \"Juan\", \"lastName\": \"Pérez\", \"alias\": \"juanperez\", \"bio\": \"Desarrollador de software\"}]"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error interno",
                                    value = "{\"status\": 500, \"message\": \"Error interno al listar perfiles de usuario\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<List<UserProfileResponse>> list() {
        logger.debug("Listando todos los perfiles de usuario");
        
        try {
            List<UserProfileResponse> profiles = userProfileService.listAll();
            logger.info("Se obtuvieron {} perfiles de usuario", profiles.size());
            return ResponseEntity.ok(profiles);
            
        } catch (UserProfileException e) {
            logger.warn("Error al listar perfiles de usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al listar perfiles de usuario: ", e);
            throw new UserProfileException("Error interno al listar perfiles de usuario");
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener perfil por ID",
            description = "Obtiene un perfil de usuario específico por su ID único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class),
                            examples = @ExampleObject(
                                    name = "Perfil de usuario",
                                    value = "{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"456e7890-e89b-12d3-a456-426614174001\", \"email\": \"usuario@ejemplo.com\", \"firstName\": \"Juan\", \"lastName\": \"Pérez\", \"alias\": \"juanperez\", \"bio\": \"Desarrollador de software\", \"avatarUrl\": \"https://ejemplo.com/avatar.jpg\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "ID inválido",
                                    value = "{\"status\": 400, \"message\": \"El ID no puede ser nulo\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Perfil no encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Perfil no encontrado",
                                    value = "{\"status\": 404, \"message\": \"Perfil de usuario no encontrado con ID: 123e4567-e89b-12d3-a456-426614174000\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<UserProfileResponse> getById(
            @Parameter(description = "ID único del perfil de usuario", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        logger.debug("Obteniendo perfil de usuario por ID: {}", id);
        
        try {
            validateId(id);
            UserProfileResponse profile = userProfileService.getById(id);
            logger.info("Perfil de usuario obtenido exitosamente con ID: {}", id);
            return ResponseEntity.ok(profile);
            
        } catch (ValidationException | ResourceNotFoundException e) {
            logger.warn("Error al obtener perfil por ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al obtener perfil por ID {}: ", id, e);
            throw new UserProfileException("Error interno al obtener perfil de usuario");
        }
    }

    @GetMapping("/by-user/{userId}")
    @Operation(
            summary = "Obtener perfil por ID de usuario",
            description = "Obtiene el perfil de usuario asociado a un ID de usuario específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class),
                            examples = @ExampleObject(
                                    name = "Perfil de usuario",
                                    value = "{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"456e7890-e89b-12d3-a456-426614174001\", \"email\": \"usuario@ejemplo.com\", \"firstName\": \"Juan\", \"lastName\": \"Pérez\", \"alias\": \"juanperez\", \"bio\": \"Desarrollador de software\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID de usuario inválido",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "ID inválido",
                                    value = "{\"status\": 400, \"message\": \"El ID del usuario no puede ser nulo\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Perfil no encontrado para el usuario",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Perfil no encontrado",
                                    value = "{\"status\": 404, \"message\": \"Perfil de usuario no encontrado para userId: 456e7890-e89b-12d3-a456-426614174001\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<UserProfileResponse> getByUserId(
            @Parameter(description = "ID único del usuario", example = "456e7890-e89b-12d3-a456-426614174001")
            @PathVariable UUID userId) {
        logger.debug("Obteniendo perfil de usuario por User ID: {}", userId);
        
        try {
            validateUserId(userId);
            UserProfileResponse profile = userProfileService.getByUserId(userId);
            logger.info("Perfil de usuario obtenido exitosamente para User ID: {}", userId);
            return ResponseEntity.ok(profile);
            
        } catch (ValidationException | ResourceNotFoundException e) {
            logger.warn("Error al obtener perfil por User ID {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al obtener perfil por User ID {}: ", userId, e);
            throw new UserProfileException("Error interno al obtener perfil de usuario por userId");
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
