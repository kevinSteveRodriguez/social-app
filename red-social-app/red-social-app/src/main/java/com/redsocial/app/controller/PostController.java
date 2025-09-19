package com.redsocial.app.controller;

import com.redsocial.app.dto.CreatePostRequest;
import com.redsocial.app.dto.PostResponse;
import com.redsocial.app.exception.AuthenticationException;
import com.redsocial.app.exception.AuthorizationException;
import com.redsocial.app.exception.ResourceNotFoundException;
import com.redsocial.app.exception.ValidationException;
import com.redsocial.app.model.User;
import com.redsocial.app.repository.UserRepository;
import com.redsocial.app.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Posts", description = "Endpoints para gestión de publicaciones")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_PAGE_SIZE = 1;

    private final PostService postService;
    private final UserRepository userRepository;

    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts")
    @Operation(
            summary = "Crear nueva publicación",
            description = "Crea una nueva publicación para el usuario autenticado. Requiere autenticación JWT."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Post creado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PostResponse.class),
                            examples = @ExampleObject(
                                    name = "Post creado",
                                    value = "{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"456e7890-e89b-12d3-a456-426614174001\", \"content\": \"Mi primer post!\", \"mediaUrl\": null, \"likesCount\": 0, \"commentsCount\": 0}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado o token inválido",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error de autenticación",
                                    value = "{\"status\": 401, \"message\": \"No autenticado\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = "{\"status\": 400, \"message\": \"El post debe tener contenido o media URL\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<PostResponse> createPost(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails principal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del post a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreatePostRequest.class),
                            examples = @ExampleObject(
                                    name = "Post con texto",
                                    value = "{\"content\": \"¡Hola mundo! Este es mi primer post.\", \"mediaUrl\": null}"
                            )
                    )
            )
            @RequestBody @Valid CreatePostRequest request) {
        logger.debug("Creando post para usuario autenticado");
        
        try {
            // Validaciones de autenticación
            validateAuthentication(principal);
            
            String email = principal.getUsername();
            User user = userRepository.findByEmail(email.toLowerCase().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            
            if (!user.isEnabled()) {
                logger.warn("Usuario inactivo intentando crear post: {}", email);
                throw new AuthorizationException("Usuario inactivo");
            }
            
            PostResponse response = postService.create(user, request);
            logger.info("Post creado exitosamente por usuario: {}", email);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (AuthenticationException | AuthorizationException | ResourceNotFoundException | ValidationException e) {
            logger.warn("Error al crear post: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear post: ", e);
            throw new ValidationException("Error interno al crear el post");
        }
    }

    @GetMapping("/posts")
    @Operation(
            summary = "Listar todas las publicaciones",
            description = "Obtiene una lista paginada de todas las publicaciones ordenadas por fecha de creación (más recientes primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de posts obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    name = "Lista de posts",
                                    value = "{\"content\": [{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"456e7890-e89b-12d3-a456-426614174001\", \"content\": \"Mi primer post!\", \"likesCount\": 5, \"commentsCount\": 2}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 10}, \"totalElements\": 1, \"totalPages\": 1}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = "{\"status\": 400, \"message\": \"El número de página debe ser mayor o igual a 0\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Page<PostResponse>> listPosts(
            @Parameter(description = "Número de página (base 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.debug("Listando posts - página: {}, tamaño: {}", page, size);
        
        try {
            // Validaciones de paginación
            validatePaginationParams(page, size);
            
            int validatedSize = Math.min(Math.max(size, MIN_PAGE_SIZE), MAX_PAGE_SIZE);
            Pageable pageable = PageRequest.of(page, validatedSize);
            
            Page<PostResponse> posts = postService.listAll(pageable);
            logger.info("Se obtuvieron {} posts de la página {}", posts.getNumberOfElements(), page);
            
            return ResponseEntity.ok(posts);
            
        } catch (ValidationException e) {
            logger.warn("Error de validación al listar posts: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al listar posts: ", e);
            throw new ValidationException("Error interno al listar posts");
        }
    }

    @GetMapping("/users/{userId}/posts")
    @Operation(
            summary = "Listar publicaciones de un usuario específico",
            description = "Obtiene una lista paginada de todas las publicaciones de un usuario específico ordenadas por fecha de creación (más recientes primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de posts del usuario obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    name = "Lista de posts del usuario",
                                    value = "{\"content\": [{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"userId\": \"456e7890-e89b-12d3-a456-426614174001\", \"content\": \"Post del usuario\", \"likesCount\": 3, \"commentsCount\": 1}], \"pageable\": {\"pageNumber\": 0, \"pageSize\": 10}, \"totalElements\": 1, \"totalPages\": 1}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos o ID de usuario inválido",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = "{\"status\": 400, \"message\": \"El ID del usuario no puede ser nulo\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Usuario no encontrado",
                                    value = "{\"status\": 404, \"message\": \"Usuario no encontrado\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Page<PostResponse>> listUserPosts(
            @Parameter(description = "ID único del usuario", example = "456e7890-e89b-12d3-a456-426614174001")
            @PathVariable UUID userId,
            @Parameter(description = "Número de página (base 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (1-100)", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.debug("Listando posts para usuario {} - página: {}, tamaño: {}", userId, page, size);
        
        try {
            // Validaciones
            validateUserId(userId);
            validatePaginationParams(page, size);
            
            int validatedSize = Math.min(Math.max(size, MIN_PAGE_SIZE), MAX_PAGE_SIZE);
            Pageable pageable = PageRequest.of(page, validatedSize);
            
            Page<PostResponse> posts = postService.listByUser(userId, pageable);
            logger.info("Se obtuvieron {} posts del usuario {} en la página {}", 
                       posts.getNumberOfElements(), userId, page);
            
            return ResponseEntity.ok(posts);
            
        } catch (ValidationException | ResourceNotFoundException e) {
            logger.warn("Error al listar posts del usuario {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al listar posts del usuario {}: ", userId, e);
            throw new ValidationException("Error interno al listar posts del usuario");
        }
    }

    /**
     * Valida que el usuario esté autenticado.
     */
    private void validateAuthentication(UserDetails principal) {
        if (principal == null) {
            throw new AuthenticationException("No autenticado");
        }
        
        if (principal.getUsername() == null || principal.getUsername().trim().isEmpty()) {
            throw new AuthenticationException("Usuario no válido");
        }
    }

    /**
     * Valida los parámetros de paginación.
     */
    private void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new ValidationException("El número de página debe ser mayor o igual a 0");
        }
        
        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            throw new ValidationException("El tamaño de página debe estar entre " + MIN_PAGE_SIZE + " y " + MAX_PAGE_SIZE);
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
