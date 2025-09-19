package com.redsocial.app.controller;

import com.redsocial.app.dto.AuthRequest;
import com.redsocial.app.dto.AuthResponse;
import com.redsocial.app.dto.RegisterRequest;
import com.redsocial.app.exception.AuthenticationException;
import com.redsocial.app.exception.ResourceNotFoundException;
import com.redsocial.app.exception.ValidationException;
import com.redsocial.app.model.User;
import com.redsocial.app.repository.UserRepository;
import com.redsocial.app.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_EMAIL_LENGTH = 255;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario con email y contraseña, devuelve un token JWT para futuras peticiones autenticadas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Respuesta exitosa",
                                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas o usuario inactivo",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error de autenticación",
                                    value = "{\"status\": 401, \"message\": \"Credenciales inválidas\", \"timestamp\": \"2024-01-01T12:00:00\"}"
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
                                    value = "{\"status\": 400, \"message\": \"El email es requerido\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales de login",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AuthRequest.class),
                            examples = @ExampleObject(
                                    name = "Credenciales de ejemplo",
                                    value = "{\"email\": \"usuario@ejemplo.com\", \"password\": \"miPassword123\"}"
                            )
                    )
            )
            @RequestBody @Valid AuthRequest request) {
        logger.debug("Intento de login para email: {}", request.getEmail());
        
        try {
            // Validaciones
            validateAuthRequest(request);
            
            String normalizedEmail = normalizeEmail(request.getEmail());
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    normalizedEmail,
                    request.getPassword()
            );
            
            authenticationManager.authenticate(authentication);
            
            User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            
            if (!user.isEnabled()) {
                logger.warn("Intento de login con usuario inactivo: {}", normalizedEmail);
                throw new AuthenticationException("Usuario inactivo");
            }
            
            String token = jwtService.generateToken(user);
            logger.info("Login exitoso para usuario: {}", normalizedEmail);
            
            return ResponseEntity.ok(new AuthResponse(token));
            
        } catch (ValidationException | ResourceNotFoundException | AuthenticationException e) {
            logger.warn("Error en login para email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (BadCredentialsException e) {
            logger.warn("Credenciales inválidas para email: {}", request.getEmail());
            throw new AuthenticationException("Credenciales inválidas");
        } catch (Exception e) {
            logger.error("Error inesperado en login para email {}: ", request.getEmail(), e);
            throw new AuthenticationException("Error interno durante el login");
        }
    }

    @PostMapping("/register")
    @Transactional
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario con email y contraseña"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o email ya registrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Error de validación",
                                    value = "{\"status\": 400, \"message\": \"El email ya está registrado\", \"timestamp\": \"2024-01-01T12:00:00\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Void> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para el registro de usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Datos de registro",
                                    value = "{\"email\": \"nuevo@usuario.com\", \"password\": \"miPassword123\"}"
                            )
                    )
            )
            @RequestBody @Valid RegisterRequest request) {
        logger.debug("Intento de registro para email: {}", request.getEmail());
        
        try {
            // Validaciones
            validateRegisterRequest(request);
            
            String normalizedEmail = normalizeEmail(request.getEmail());
            
            if (userRepository.existsByEmail(normalizedEmail)) {
                logger.warn("Intento de registro con email ya existente: {}", normalizedEmail);
                throw new ValidationException("El email ya está registrado");
            }
            
            User user = new User();
            user.setEmail(normalizedEmail);
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setActive(true);
            user.setVerified(false);
            
            userRepository.save(user);
            logger.info("Registro exitoso para usuario: {}", normalizedEmail);
            
            return ResponseEntity.status(HttpStatus.CREATED).build();
            
        } catch (ValidationException e) {
            logger.warn("Error de validación en registro para email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en registro para email {}: ", request.getEmail(), e);
            throw new ValidationException("Error interno durante el registro");
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtener información del usuario autenticado",
            description = "Devuelve el email del usuario autenticado basado en el token JWT"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Información del usuario obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Email del usuario",
                                    value = "\"usuario@ejemplo.com\""
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
            )
    })
    public ResponseEntity<String> me(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {
        logger.debug("Verificando información del usuario autenticado");
        
        try {
            if (user == null) {
                logger.warn("Intento de acceso sin autenticación");
                throw new AuthenticationException("No autenticado");
            }
            
            logger.info("Información del usuario obtenida: {}", user.getUsername());
            return ResponseEntity.ok(user.getUsername());
            
        } catch (AuthenticationException e) {
            logger.warn("Error de autenticación en /me: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en /me: ", e);
            throw new AuthenticationException("Error interno al obtener información del usuario");
        }
    }

    /**
     * Normaliza el email (minúsculas y sin espacios).
     */
    private String normalizeEmail(String email) {
        return email != null ? email.toLowerCase().trim() : null;
    }

    /**
     * Valida el request de autenticación.
     */
    private void validateAuthRequest(AuthRequest request) {
        if (request == null) {
            throw new ValidationException("El request no puede ser nulo");
        }
        
        if (!StringUtils.hasText(request.getEmail())) {
            throw new ValidationException("El email es requerido");
        }
        
        if (!StringUtils.hasText(request.getPassword())) {
            throw new ValidationException("La contraseña es requerida");
        }
        
        if (request.getEmail().length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException("El email no puede exceder " + MAX_EMAIL_LENGTH + " caracteres");
        }
    }

    /**
     * Valida el request de registro.
     */
    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new ValidationException("El request no puede ser nulo");
        }
        
        if (!StringUtils.hasText(request.getEmail())) {
            throw new ValidationException("El email es requerido");
        }
        
        if (!StringUtils.hasText(request.getPassword())) {
            throw new ValidationException("La contraseña es requerida");
        }
        
        if (request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        
        if (request.getEmail().length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException("El email no puede exceder " + MAX_EMAIL_LENGTH + " caracteres");
        }
        
        // Validación básica de formato de email
        if (!request.getEmail().contains("@") || !request.getEmail().contains(".")) {
            throw new ValidationException("El formato del email no es válido");
        }
    }
}
